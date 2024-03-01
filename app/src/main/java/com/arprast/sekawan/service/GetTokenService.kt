package com.arprast.sekawan.service

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.arprast.sekawan.httpClient.SekawanBEApi
import com.arprast.sekawan.paymo.Request
import com.arprast.sekawan.paymo.SecurityUtils
import com.arprast.sekawan.paymo.model.AuthCredRequest
import com.arprast.sekawan.paymo.request.GetTokenRequest
import com.arprast.sekawan.paymo.request.GetTokenRequestDetail
import com.arprast.sekawan.paymo.response.TokenResponse
import com.arprast.sekawan.repository.RealmDBRepository
import com.arprast.sekawan.repository.tableModel.AuthTable
import com.arprast.sekawan.util.BOARDCAST_MESSGAE_MAIN
import com.arprast.sekawan.util.BOARDCAST_MESSGAE_MAIN_PUT_EXTRA_USER_ID
import com.arprast.sekawan.util.PreferanceVariable
import com.arprast.sekawan.util.config_server_client_id
import com.arprast.sekawan.util.config_server_client_key
import com.arprast.sekawan.util.config_server_client_key_pattern
import com.arprast.sekawan.util.config_server_encryption_key
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.UUID


class GetTokenService(
    private val config: SharedPreferences,
    private val gson: Gson,
    private var sekawanBEApi: SekawanBEApi,
    private var context: Context
) {

    fun initGetToken(phoneNo: String, password: String) {

        val requestTime = System.currentTimeMillis()
        val encryptionKey = config.getString(config_server_encryption_key, null)!!
        val clientId = config.getString(config_server_client_id, null)!!
        val clientKeyPattern = config.getString(config_server_client_key_pattern, null)!!
        val clientKey = config.getString(config_server_client_key, null)!!
        val getTokenRequest = buildGetTokenRequest(phoneNo, password, encryptionKey, requestTime)

        //http async process
        val tokenResponse = sekawanBEApi.getToken(
            getTokenRequest.requestId,
            clientId,
            buildHMACSHA256GetTokenRequest(clientId, clientKeyPattern, clientKey, getTokenRequest),
            getTokenRequest
        )

        tokenResponse.enqueue(object : Callback<TokenResponse?> {

            override fun onResponse(
                call: Call<TokenResponse?>,
                response: Response<TokenResponse?>
            ) {
                if (response.body() != null && response.body()!!.responseCode == 200 && response.body()!!.body.token != null) {
                    saveAuthToken(requestTime, response.body()!!)
                    broadcastMessage(response.body()!!)
                }
            }

            override fun onFailure(call: Call<TokenResponse?>, t: Throwable) {
                Log.d(PreferanceVariable.DEBUG_NAME, "error response get token", t)
            }
        })
    }

    private fun saveAuthToken(requestTime: Long, response: TokenResponse) {
        val authTable = AuthTable()
        authTable.createDate = Date(requestTime)
        authTable.updateDate = Date(requestTime)
        authTable.token = response.body.token
        authTable.userId = response.body.userId
        RealmDBRepository().saveAuth(authTable)
    }

    private fun broadcastMessage(response: TokenResponse) {
        val intent = Intent(BOARDCAST_MESSGAE_MAIN)
        intent.putExtra(BOARDCAST_MESSGAE_MAIN_PUT_EXTRA_USER_ID, response.body.userId)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    private fun buildGetTokenRequest(
        phoneNo: String,
        password: String,
        encryptionKey: String,
        requestTime: Long
    ): GetTokenRequest {

        val authReq = AuthCredRequest()
        authReq.phoneNo = phoneNo
        authReq.password = password
        val authReqInJson = gson.toJson(authReq).toString()

        var cred: String? = null
        val encryptionKeyFinal = SecurityUtils.encryptionKeyGenerator(encryptionKey, requestTime)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cred = SecurityUtils.encryptedAES128(encryptionKeyFinal, authReqInJson)
        }

        val getTokenRequestDetail = GetTokenRequestDetail()
        getTokenRequestDetail.cred = cred

        val getTokenRequest = GetTokenRequest()
        getTokenRequest.requestId = UUID.randomUUID().toString()
        getTokenRequest.type = Request.TYPE_GENERATE_TOKEN
        getTokenRequest.requestTime = requestTime
        getTokenRequest.body = getTokenRequestDetail
        return getTokenRequest
    }

    private fun buildHMACSHA256GetTokenRequest(
        clientId: String,
        clientKeyPattern: String,
        clientKey: String,
        getTokenRequest: GetTokenRequest
    ): String {
        val secretKeySHA256 = clientId + clientKeyPattern + clientKey
        return SecurityUtils.hmacSHA256(secretKeySHA256, gson.toJson(getTokenRequest))
    }

}
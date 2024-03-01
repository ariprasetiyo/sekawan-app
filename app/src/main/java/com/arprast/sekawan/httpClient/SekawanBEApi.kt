package com.arprast.sekawan.httpClient

import com.arprast.sekawan.paymo.request.GetTokenRequest
import com.arprast.sekawan.paymo.response.TokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface SekawanBEApi {
    @Headers("Content-type: application/json")
    @POST("/public/token")
    fun getToken(@Header("Msg-Id") msgId : String, @Header("Client-Id") clientId : String, @Header("Signature") signature : String, @Body body: GetTokenRequest): Call<TokenResponse>

}
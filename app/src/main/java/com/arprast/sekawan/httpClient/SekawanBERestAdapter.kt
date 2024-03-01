package com.arprast.sekawan.httpClient

import android.content.SharedPreferences
import com.arprast.sekawan.util.HostnameVerifier
import com.arprast.sekawan.util.config_server_request_timeout
import com.arprast.sekawan.util.config_server_url
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SekawanBERestAdapter(
    val config: SharedPreferences,
    val gson: Gson,
    val verifiedDomainList: List<String>
) {

    fun getInterface(): SekawanBEApi {
        val serverUrl = config.getString(config_server_url, null)!!
        val timeout = config.getLong(config_server_request_timeout, 1000L)
        return createRetrofit(
            serverUrl,
            verifiedDomainList,
            timeout
        )
    }

    private fun createRetrofit(
        url: String,
        verifiedDomain: List<String>,
        timeoutMillis: Long
    ): SekawanBEApi {
        val interceptor = getInterceptor()
        val client = OkHttpClient.Builder()
            .hostnameVerifier(HostnameVerifier(verifiedDomain))
            .addInterceptor(interceptor)
            .connectTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
            .readTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
            .writeTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
        return retrofit.create(SekawanBEApi::class.java)
    }

    private fun getInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }
}
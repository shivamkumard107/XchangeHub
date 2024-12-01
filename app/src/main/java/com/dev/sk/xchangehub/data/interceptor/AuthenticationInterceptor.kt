package com.dev.sk.xchangehub.data.interceptor

import com.dev.sk.xchangehub.di.ApiKey
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

const val APP_ID_PARAM = "app_id"

class AuthenticationInterceptor @Inject constructor(
    @ApiKey private val apiKey: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        val urlWithApiKey: HttpUrl = originalUrl.newBuilder()
            .addQueryParameter(APP_ID_PARAM, apiKey)
            .build()
        val newRequest = originalRequest.newBuilder()
            .url(urlWithApiKey)
            .build()
        return chain.proceed(newRequest)
    }

}
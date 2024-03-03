package dev.farukh.network.utils

import okhttp3.Interceptor
import okhttp3.Response

class DaDataInterceptor(
    private val token: String = "",
    private val secret: String = ""
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Token $token")
            .addHeader("X-Secret", secret)
            .build()
        return chain.proceed(newRequest)
    }
}
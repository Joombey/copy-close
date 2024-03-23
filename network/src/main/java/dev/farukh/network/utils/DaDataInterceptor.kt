package dev.farukh.network.utils

import dev.farukh.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class DaDataInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Token ${BuildConfig.DaDataApiKey}")
            .addHeader("X-Secret", BuildConfig.DaDataSecret)
            .build()
        return chain.proceed(newRequest)
    }
}
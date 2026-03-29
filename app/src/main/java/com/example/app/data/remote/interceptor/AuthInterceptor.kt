package com.example.app.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            val req = chain.request().url.newBuilder().apply {
                addQueryParameter(
                    "apiKey", "90c806882637469f826b1ac8b2b3ec0b"
                )
                addQueryParameter("country", "us")
            }.build()
            url(req)
        }.build()
        return chain.proceed(request)
    }
}
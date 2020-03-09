package com.aescolar.apiclient

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor(
    private val publicKey: String,
    private val privateKey: String
) : Interceptor {

    private val authHashGenerator = AuthHashGenerator()

    private val time: Long
        get() = System.currentTimeMillis()

    override fun intercept(chain: Interceptor.Chain): Response {
        val timeStamp = time.toString()

        val hash = authHashGenerator.generateHash(timeStamp, publicKey, privateKey)

        var request: Request = chain.request()
        val url: HttpUrl = request.url()
            .newBuilder()
            .addQueryParameter("ts", timeStamp)
            .addQueryParameter("apikey", publicKey)
            .addQueryParameter("hash", hash)
            .build()
        request = request.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}

package com.aescolar.apimodule

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor(
    private val publicKey: String,
    private val privateKey: String,
    private val timeProvider: TimeProvider
) : Interceptor {

    private val authHashGenerator = AuthHashGenerator()

    override fun intercept(chain: Interceptor.Chain): Response {
        val timeStamp = timeProvider.time.toString()
        // val hash = generateMd5(timeStamp)

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
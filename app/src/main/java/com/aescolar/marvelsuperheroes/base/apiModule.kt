package com.aescolar.marvelsuperheroes.base

import com.aescolar.apiclient.ApiClient
import com.aescolar.apiclient.AuthInterceptor
import com.aescolar.apiclient.InterceptorProvider
import com.aescolar.marvelsuperheroes.BuildConfig
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module

val apiModule = module {

    single {
        val interceptors = mutableListOf<Interceptor>().apply {
            add(AuthInterceptor(BuildConfig.MARVEL_PUBLIC_KEY, BuildConfig.MARVEL_PRIVATE_KEY))
            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
                add(loggingInterceptor)
            }
        }
        InterceptorProvider(interceptors)
    }
    single { ApiClient("https://gateway.marvel.com/v1/public/", get()) }
}

package com.aescolar.apimodule

import arrow.core.Either
import com.aescolar.apimodule.model.CharacterDataWrapper
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient(BASE_URL: String, interceptorProvider: InterceptorProvider) {

    sealed class ApiError {
        data class UnknownError(val code: Int) : ApiError()
        object NotFoundError : ApiError()
        object NetworkError : ApiError()
    }

    private val characterService: CharacterService by lazy {
        retrofit.create(CharacterService::class.java)
    }

    private val retrofit: Retrofit by lazy {
        val okHttpBuilder = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)

        interceptorProvider.interceptors.forEach {
            okHttpBuilder.addInterceptor(it)
        }

        val okHttpClient = okHttpBuilder.build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    fun fetchCharacters(page: Int): Either<ApiError, CharacterDataWrapper> {
        return characterService.fetchCharacters(page).safeExecute()
    }

    private fun <T> Call<T>.safeExecute(): Either<ApiError, T> {
        return try {
            val response = execute()
            parseResponse(response)
        } catch (e: Exception) {
            Either.left(ApiError.NetworkError)
        }
    }

    private fun <T> parseResponse(response: Response<T>): Either<ApiError, T> = when {
        response.code() == 404 -> Either.left(ApiError.NotFoundError)
        response.code() > 400 -> Either.left(ApiError.UnknownError(response.code()))
        response.body() == null -> Either.left(ApiError.UnknownError(response.code()))
        else -> Either.right(response.body()!!)
    }

}


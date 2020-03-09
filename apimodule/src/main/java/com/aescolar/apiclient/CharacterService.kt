package com.aescolar.apiclient

import com.aescolar.apiclient.model.CharactersWrapperDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CharacterService {
    @GET(value = "characters")
    fun fetchCharacters(
        @Query("offset") page: Int? = null,
        @Query("nameStartsWith") filter: String? = null
    ): Call<CharactersWrapperDto>

    @GET(value = "characters/{id}")
    fun findCharacter(@Path("id") characterId: String): Call<CharactersWrapperDto>


    @GET(value = "characters")
    suspend fun suspendFetchCharacters(
        @Query("offset") page: Int? = null,
        @Query("nameStartsWith") filter: String? = null
    ): Response<CharactersWrapperDto>
}

package com.aescolar.apimodule

import com.aescolar.apimodule.model.CharacterDataWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CharacterService {
    @GET(value = "characters")
    fun fetchCharacters(@Query("offset") page: Int): Call<CharacterDataWrapper>

    //  @GET(value = "/characters/{id}")
    //  fun findCharacter(@Path("id") characterId: String)
}
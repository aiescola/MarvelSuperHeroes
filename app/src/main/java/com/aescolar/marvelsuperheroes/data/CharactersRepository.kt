package com.aescolar.marvelsuperheroes.data

import arrow.core.Either
import com.aescolar.apiclient.ApiClient
import com.aescolar.marvelsuperheroes.domain.model.Character
import com.aescolar.marvelsuperheroes.domain.model.Page
import com.aescolar.marvelsuperheroes.domain.model.asDomainModel

class CharactersRepository(
    private val apiClient: ApiClient
) {

    private var currentOffset: Int? = null
    private var allCharacters = mutableListOf<Character>()
    private var currentFilter: String? = null

    fun fetchPage(filter: String? = null): Either<ApiClient.ApiError, Page> {
        refreshCurrentFilter(filter)

        val charactersFromApi = apiClient.fetchCharacters(currentOffset, currentFilter)
        return charactersFromApi.map {
            currentOffset = it.data.offset.toInt() + it.data.count.toInt()
            val moreToLoad = currentOffset!! < it.data.total.toInt()

            allCharacters.addAll(it.asDomainModel())
            Page(
                moreToLoad,
                allCharacters
            )
        }
    }

    private fun refreshCurrentFilter(filter: String?) {
        if (filter != currentFilter) {
            currentOffset = 0
            currentFilter = filter
            allCharacters.clear()
        }
    }

    fun findCharacter(id: String): Either<ApiClient.ApiError, Character> {
        return apiClient.findCharacterById(id).map {
            it.asDomainModel().first()
        }
    }
}

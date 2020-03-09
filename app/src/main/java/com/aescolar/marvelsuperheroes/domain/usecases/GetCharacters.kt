package com.aescolar.marvelsuperheroes.domain.usecases

import arrow.core.Either
import com.aescolar.apiclient.ApiClient
import com.aescolar.marvelsuperheroes.data.CharactersRepository
import com.aescolar.marvelsuperheroes.domain.model.Page

class GetCharacters(private val charactersRepository: CharactersRepository) {

    operator fun invoke(filter: String? = null): Either<ApiClient.ApiError, Page> =
        charactersRepository.fetchPage(filter)
}

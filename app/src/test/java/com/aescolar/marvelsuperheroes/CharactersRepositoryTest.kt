package com.aescolar.marvelsuperheroes

import arrow.core.Either
import arrow.core.orNull
import com.aescolar.apiclient.ApiClient
import com.aescolar.apiclient.model.CharactersWrapperDto
import com.aescolar.marvelsuperheroes.data.CharactersRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class CharactersRepositoryTest {

    @MockK
    private lateinit var apiClientMock: ApiClient

    private lateinit var charactersRepository: CharactersRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        charactersRepository = CharactersRepository(apiClientMock)
    }

    @Test
    fun `fetch a single page`() {
        val response = givenACorrectCharactersWrapper()
        val expectedSize = response.orNull()!!.data.results.size

        val result = charactersRepository.fetchPage().orNull()
        val resultsSize = result?.data?.size ?: 0

        verify { apiClientMock.fetchCharacters(null) }
        assertNotNull(result)
        assertEquals(expectedSize, resultsSize)
        assertEquals(false, result?.moreToLoad)
    }

    @Test
    fun `fetch two pages`() {
        val response = givenSeveralCharactersWrapper()

        val firstOffset = response.first().orNull()!!.data.count.toInt()
        val expectedSize = response.sumBy {
            it.orNull()!!.data.results.size
        }

        val result1 = charactersRepository.fetchPage().orNull()

        assertNotNull(result1)
        assertEquals(true, result1!!.moreToLoad)

        val result2 = charactersRepository.fetchPage().orNull()

        assertNotNull(result2)
        val resultSize = result2!!.data.size

        assertEquals(expectedSize, resultSize)

        verifyOrder {
            apiClientMock.fetchCharacters()
            apiClientMock.fetchCharacters(eq(firstOffset))
        }
    }

    @Test
    fun `fetch page returns NetworkError`() {
        givenApiError(ApiClient.ApiError.NetworkError)

        val result = charactersRepository.fetchPage()

        assert(result.isLeft())
        assertEquals(Either.left(ApiClient.ApiError.NetworkError), result)
    }

    @Test
    fun `fetch page returns NotFoundError`() {
        givenApiError(ApiClient.ApiError.NotFoundError)

        val result = charactersRepository.fetchPage()

        assert(result.isLeft())
        assertEquals(Either.left(ApiClient.ApiError.NotFoundError), result)
    }

    @Test
    fun `fetch page returns UnknownError`() {
        val code = 400
        givenApiError(ApiClient.ApiError.UnknownError(code))

        val result = charactersRepository.fetchPage()

        assert(result.isLeft())
        assertEquals(Either.left(ApiClient.ApiError.UnknownError(code)), result)
    }

    private fun givenACorrectCharactersWrapper(): Either<ApiClient.ApiError, CharactersWrapperDto> {
        val response = Either.right(CharactersWrapperObjectMother.singlePageCharactersWrapperDto)
        every { apiClientMock.fetchCharacters(any()) } returns response
        return response
    }

    private fun givenSeveralCharactersWrapper(): List<Either<ApiClient.ApiError, CharactersWrapperDto>> {
        val charactersResponse = CharactersWrapperObjectMother.multiPageCharactersWrapperDto.map {
            Either.right(it)
        }
        every { apiClientMock.fetchCharacters(any()) } returnsMany charactersResponse
        return charactersResponse
    }

    private fun givenApiError(error: ApiClient.ApiError): Either<ApiClient.ApiError, CharactersWrapperDto> {
        val response = Either.left(error)
        every { apiClientMock.fetchCharacters(any()) } returns response
        return response
    }
}

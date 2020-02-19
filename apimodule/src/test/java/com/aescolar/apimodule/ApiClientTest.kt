package com.aescolar.apimodule

import arrow.core.left
import arrow.core.orNull
import arrow.core.right
import com.aescolar.apimodule.model.CharacterDataWrapper
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class ApiClientTest : MockWebServerTest() {

    private val mockTimeProvider = mockk<TimeProvider>()
    private val mockInterceptorProvider = mockk<InterceptorProvider>()

    private lateinit var apiClient: ApiClient

    @Before
    override fun setUp() {
        super.setUp()
        val mockWebServerEndpoint = baseEndpoint
        apiClient = ApiClient(mockWebServerEndpoint, mockInterceptorProvider)
    }

    @Test
    fun `request is sent and method is get`() {
        givenResponseWithoutInterceptors()
        enqueueMockResponse(200, "all_characters_200.json")
        apiClient.fetchCharacters(0)

        assertGetRequestSentTo("/characters?offset=0")
    }

    @Test
    fun `get request is sent with headers`() {
        givenResponseWithInterceptors()
        enqueueMockResponse(200, "all_characters_200.json")

        apiClient.fetchCharacters(0)

        assertGetRequestSentTo("/characters?offset=0&ts=1&apikey=1234&hash=ffd275c5130566a2916217b101f26150")
    }

    @Test
    fun `returns network products when status 200 and json okay`() {
        givenResponseWithoutInterceptors()
        enqueueMockResponse(200, "all_characters_200.json")

        val response = apiClient.fetchCharacters(0)

        assert(response.isRight())
    }

    @Test
    fun `returns error when status 400`() {
        givenResponseWithoutInterceptors()
        enqueueMockResponse(400)

        val response = apiClient.fetchCharacters(0)

        assertEquals(ApiClient.ApiError.UnknownError(400).left(), response)
    }

    @Test
    fun `returns error when status 404`() {
        givenResponseWithoutInterceptors()
        enqueueMockResponse(404)

        val response = apiClient.fetchCharacters(0)

        assertEquals(ApiClient.ApiError.NotFoundError.left(), response)
    }

    private fun givenResponseWithoutInterceptors() {
        every { mockInterceptorProvider.interceptors } returns emptyList()
    }

    private fun givenResponseWithInterceptors() {
        every { mockTimeProvider.time } returns 1L
        val authInterceptor = AuthInterceptor("1234", "abcd", mockTimeProvider)
        every { mockInterceptorProvider.interceptors }.returns(listOf(authInterceptor))
    }

}
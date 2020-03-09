package com.aescolar.apiclient

import arrow.core.left
import arrow.core.orNull
import com.aescolar.apiclient.base.MockWebServerTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ApiClientTest : MockWebServerTest() {

    private val mockAuthInterceptor =
        spyk(AuthInterceptor("publicKey", "privateKey"), recordPrivateCalls = true)

    private val mockInterceptorProvider = mockk<InterceptorProvider>()

    private lateinit var apiClient: ApiClient

    @Before
    override fun setUp() {
        super.setUp()

        val mockWebServerEndpoint = baseEndpoint
        apiClient = ApiClient(mockWebServerEndpoint, mockInterceptorProvider)
    }

    @Test
    fun `request first page is sent and method is get`() {
        givenResponseWithoutInterceptors()
        enqueueMockResponse(200, "all_characters_200.json")
        apiClient.fetchCharacters()

        assertGetRequestSentTo("/characters")
    }

    @Test
    fun `request second page is sent and method is get`() {
        givenResponseWithoutInterceptors()
        enqueueMockResponse(200, "all_characters_200.json")
        apiClient.fetchCharacters(1)

        assertGetRequestSentTo("/characters?offset=1")
    }

    @Test
    fun `valid query params are sent`() {
        givenResponseWithInterceptors()
        enqueueMockResponse(200)

        apiClient.fetchCharacters(0)

        assertRequestSentToContains("offset", "ts", "apikey", "hash")
    }

    @Test
    fun `returns all characters when status 200 and json okay`() {
        givenResponseWithoutInterceptors()
        enqueueMockResponse(200, "all_characters_200.json")

        val response = apiClient.fetchCharacters(0)

        val character = response.orNull()
        character?.data?.count
        assert(response.isRight())
        assertEquals(3, character?.data?.results?.size)
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
        every { mockAuthInterceptor getProperty "time" } propertyType Long::class answers { 1L }

        every { mockInterceptorProvider.interceptors } returns listOf(mockAuthInterceptor)
    }
}

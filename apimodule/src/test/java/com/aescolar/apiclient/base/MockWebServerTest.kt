package com.aescolar.apiclient.base

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before

open class MockWebServerTest {

    private var server: MockWebServer = MockWebServer()

    @Before
    open fun setUp() {
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    fun enqueueMockResponse(code: Int = 200, fileName: String? = null) {
        val mockResponse = MockResponse()
        val fileContent = JsonReader.getContentFromFile(fileName)
        mockResponse.setResponseCode(code)
        mockResponse.setBody(fileContent)
        server.enqueue(mockResponse)
    }

    protected fun assertGetRequestSentTo(url: String) {
        val request = server.takeRequest()
        assertEquals(url, request.path)
        assertEquals("GET", request.method)
    }

    protected fun assertRequestSentToContains(vararg paths: String) {
        val request = server.takeRequest()

        for (path in paths) {
            Assert.assertThat(request.path, containsString(path))
        }
    }

    protected val baseEndpoint: String
        get() = server.url("/").toString()
}

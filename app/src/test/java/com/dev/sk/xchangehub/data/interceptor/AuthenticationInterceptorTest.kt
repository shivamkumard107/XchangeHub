package com.dev.sk.xchangehub.data.interceptor

import com.dev.sk.xchangehub.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class AuthenticationInterceptorTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var client: OkHttpClient

    private val SOME_API_KEY = "SOME_API_KEY"

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val authenticationInterceptor = AuthenticationInterceptor(SOME_API_KEY)
        client = OkHttpClient.Builder()
            .addInterceptor(authenticationInterceptor)
            .build()
    }

    @Test
    fun `test interceptor adds API key as query parameter`() {
        // Arrange
        val testUrl = mockWebServer.url("/test")
        val request = Request.Builder()
            .url(testUrl)
            .build()

        mockWebServer.enqueue(MockResponse().setResponseCode(200))

        // Act
        client.newCall(request).execute()

        // Assert
        val recordedRequest = mockWebServer.takeRequest()
        val requestUrl = recordedRequest.requestUrl

        assertTrue(
            "API key should be added as query parameter",
            requestUrl?.queryParameterNames?.contains(APP_ID_PARAM) == true
        )
        assertTrue(
            "API key should have correct value",
            requestUrl?.queryParameter(APP_ID_PARAM) == SOME_API_KEY
        )
    }
}
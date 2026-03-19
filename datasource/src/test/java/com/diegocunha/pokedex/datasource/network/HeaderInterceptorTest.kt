package com.diegocunha.pokedex.datasource.network

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Test

class HeaderInterceptorTest {

    private val interceptor = HeaderInterceptor()

    private val mockResponse = mockk<Response>()
    private val chain = mockk<Interceptor.Chain>()
    private val capturedRequest = slot<Request>()

    @Test
    fun `adds Accept header with application json`() {
        val originalRequest = Request.Builder().url("https://pokeapi.co/api/v2/pokemon").build()
        every { chain.request() } returns originalRequest
        every { chain.proceed(capture(capturedRequest)) } returns mockResponse

        interceptor.intercept(chain)

        assertEquals("application/json", capturedRequest.captured.header("Accept"))
    }

    @Test
    fun `adds Content-Type header with application json`() {
        val originalRequest = Request.Builder().url("https://pokeapi.co/api/v2/pokemon").build()
        every { chain.request() } returns originalRequest
        every { chain.proceed(capture(capturedRequest)) } returns mockResponse

        interceptor.intercept(chain)

        assertEquals("application/json", capturedRequest.captured.header("Content-Type"))
    }

    @Test
    fun `proceeds with modified request`() {
        val originalRequest = Request.Builder().url("https://pokeapi.co/api/v2/pokemon").build()
        every { chain.request() } returns originalRequest
        every { chain.proceed(any()) } returns mockResponse

        interceptor.intercept(chain)

        verify(exactly = 1) { chain.proceed(any()) }
    }

    @Test
    fun `returns response from chain`() {
        val originalRequest = Request.Builder().url("https://pokeapi.co/api/v2/pokemon").build()
        every { chain.request() } returns originalRequest
        every { chain.proceed(any()) } returns mockResponse

        val result = interceptor.intercept(chain)

        assertEquals(mockResponse, result)
    }

    @Test
    fun `preserves original request url`() {
        val url = "https://pokeapi.co/api/v2/pokemon/1"
        val originalRequest = Request.Builder().url(url).build()
        every { chain.request() } returns originalRequest
        every { chain.proceed(capture(capturedRequest)) } returns mockResponse

        interceptor.intercept(chain)

        assertEquals(url, capturedRequest.captured.url.toString())
    }
}
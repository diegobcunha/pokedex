package com.diegocunha.pokedex.datasource.network

import com.diegocunha.pokedex.core.Resource
import com.diegocunha.pokedex.core.coroutines.DispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

private val testDispatchers = object : DispatchersProvider {
    override fun io(): CoroutineDispatcher = Dispatchers.Unconfined
    override fun main(): CoroutineDispatcher = Dispatchers.Unconfined
}

class SafeApiCallTest {

    @Test
    fun `safeApiCall returns Success when call succeeds`() = runTest {
        val result = safeApiCall(testDispatchers) { "response" }
        assertTrue(result is Resource.Success)
        assertEquals("response", (result as Resource.Success).data)
    }

    @Test
    fun `safeApiCall returns Error when call throws`() = runTest {
        val exception = RuntimeException("network error")
        val result = safeApiCall(testDispatchers) { throw exception }
        assertTrue(result is Resource.Error)
        assertSame(exception, (result as Resource.Error).exception)
    }

    @Test
    fun `safeApiCall returns Success with null value`() = runTest {
        val result = safeApiCall(testDispatchers) { null }
        assertTrue(result is Resource.Success)
        assertEquals(null, (result as Resource.Success).data)
    }
}

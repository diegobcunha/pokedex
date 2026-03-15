package com.diegocunha.pokedex.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ResourceTest {

    @Test
    fun `toResource returns Success when Result is success`() {
        val result = Result.success("data")
        val resource = result.toResource()
        assertTrue(resource is Resource.Success)
        assertEquals("data", (resource as Resource.Success).data)
    }

    @Test
    fun `toResource returns Error when Result is failure`() {
        val exception = RuntimeException("error")
        val result = Result.failure<String>(exception)
        val resource = result.toResource()
        assertTrue(resource is Resource.Error)
        assertSame(exception, (resource as Resource.Error).exception)
    }

    @Test
    fun `Resource Success holds correct data`() {
        val resource: Resource<Int> = Resource.Success(42)
        assertEquals(42, (resource as Resource.Success).data)
    }

    @Test
    fun `Resource Error holds correct exception`() {
        val exception = IllegalStateException("fail")
        val resource: Resource<Nothing> = Resource.Error(exception)
        assertSame(exception, (resource as Resource.Error).exception)
    }

    @Test
    fun `Resource Loading is singleton`() {
        assertSame(Resource.Loading, Resource.Loading)
    }
}

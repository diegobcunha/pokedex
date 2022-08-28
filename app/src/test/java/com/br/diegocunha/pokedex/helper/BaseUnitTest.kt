package com.br.diegocunha.pokedex.helper

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Rule

@ExperimentalCoroutinesApi
open class BaseUnitTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = MainCoroutineTestRule()

    val testDispatchersProvider by lazy {
        coroutinesTestRule.dispatchersProvider
    }

    fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) =
        coroutinesTestRule.runBlockingTest(block)
}
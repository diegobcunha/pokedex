package com.br.diegocunha.pokedex.templates

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.MutableStateFlow

@JvmInline
value class GetState<out T> private constructor(val value: Any) {

    /**
     * When the current status is initial the value is GetInitialState
     * @throws IllegalStateException if current status is not initial
     */
    val initial: GetInitial
        get() {
            checkStatus(GetStatus.INITIAL)
            return value as GetInitial
        }

    /**
     * When the current status is a failure the value is GetFailureState
     * @throws IllegalStateException if current status is not failure
     */
    val failure: GetFailure
        get() {
            checkStatus(GetStatus.FAILURE)
            return value as GetFailure
        }

    /**
     * When the current status is a success the value is T
     * @throws IllegalStateException if current status is not success
     */
    val success: T
        get() {
            checkStatus(GetStatus.SUCCESS)
            return value as T
        }

    fun currentStatus(): GetStatus {
        return when (value) {
            is GetInitial -> GetStatus.INITIAL
            is GetFailure -> GetStatus.FAILURE
            else -> GetStatus.SUCCESS
        }
    }

    override fun toString(): String {
        return value.toString()
    }

    /**
     * Enabling only three ways to instantiate a DefaultGetState
     */
    companion object {

        /**
         * Creates an instance of get state in initial status, using the default values
         */
        fun <T> initial(state: GetInitial = GetInitial()) =
            GetState<T>(state)

        /**
         * Creates an instance of get state in failure status, using default values
         */
        fun <T> failure(throwable: Throwable) = failure<T>(GetFailure(throwable))

        /**
         * Creates an instance of get state in failure status
         */
        fun <T> failure(state: GetFailure) =
            GetState<T>(state)

        /**
         * Creates an instance of
         */
        fun <T> success(state: T) =
            GetState<T>(value = state as Any)
    }

    /**
     * Check the current status and throws an exception
     */
    private fun checkStatus(expected: GetStatus) {
        check(currentStatus() == expected) {
            "Invalid state access: current ${currentStatus()} - expected $expected"
        }
    }
}

/**
 * Represents the status of some asynchronous operation
 */
enum class GetStatus {
    /**
     * Initial status to indicate to user some empty or loading UI states
     */
    INITIAL,

    /**
     * Indicates a failure to show some exception case to UI state
     */
    FAILURE,

    /**
     * Used to show the expected content of UI
     */
    SUCCESS
}

/**
 * Representation of the default failure state
 */
@Immutable
data class GetFailure(val throwable: Throwable, val retrying: Boolean = false)

/**
 * Representation of the default initial state
 */
@Immutable
data class GetInitial(val loading: Boolean = true)

/**
 * Default refresh function used when the get use case are in GetState.Failure to apply the
 * following behaviors:
 * - updates the ui about the new retrying state of failure
 * - try the request again
 * - apply the default retry polocy
 */
suspend fun <T> MutableStateFlow<GetState<T>>.retryState(
    fetch: suspend () -> GetState<T>
) {
    val failureRetrying = value.failure.copy(retrying = true)
    value = GetState.failure(failureRetrying)
    value = fetch()
}

/**
 * Converts a result to a GetState
 */
fun <T> Result<T>.toGetState(): GetState<T> {
    return fold(
        onSuccess = { GetState.success(it) },
        onFailure = { GetState.failure(it) }
    )
}

/**
 * Map function to map response
 */
fun <T, R> GetState<T>.map(mapBlock: (T?) -> R): GetState<R> {
    return when (currentStatus()) {
        GetStatus.FAILURE -> GetState.failure(failure)
        GetStatus.INITIAL -> GetState.initial(initial)
        GetStatus.SUCCESS -> {
            val r = mapBlock(success)
            GetState.success(r)
        }
    }
}
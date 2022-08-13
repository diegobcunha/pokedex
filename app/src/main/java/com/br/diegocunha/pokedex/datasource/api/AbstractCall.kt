package com.br.diegocunha.pokedex.datasource.api

import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class AbstractCall<T, R>(proxy: Call<T>) : CallDelegate<T, R>(proxy) {

    override fun enqueueImpl(callback: Callback<R>) = proxy.enqueue(object : Callback<T> {

        override fun onResponse(call: Call<T>, response: Response<T>) {
            val result = if (response.isSuccessful) {
                mapSuccess(response)
            } else {
                mapError(response)
            }
            callback.onResponse(this@AbstractCall, Response.success(result))
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            val result = mapFailure(t)
            callback.onResponse(this@AbstractCall, Response.success(result))
        }
    })

    protected abstract fun mapSuccess(response: Response<T>): R
    protected abstract fun mapError(response: Response<T>): R
    protected abstract fun mapFailure(t: Throwable): R

    override fun timeout(): Timeout {
        return proxy.timeout()
    }
}
package com.br.diegocunha.pokedex.datasource.api

import retrofit2.Call
import retrofit2.Response

class ResultCall<T>(proxy: Call<T>) : AbstractCall<T, Result<T>>(proxy) {

    override fun cloneImpl(): Call<Result<T>> = ResultCall<T>(proxy.clone())

    override fun mapSuccess(response: Response<T>): Result<T> = Result.success(response.body()!!)

    override fun mapError(response: Response<T>): Result<T> {
        return Result.failure(Exception(response.errorBody()?.string()))
    }

    override fun mapFailure(t: Throwable): Result<T> = Result.failure(t)
}
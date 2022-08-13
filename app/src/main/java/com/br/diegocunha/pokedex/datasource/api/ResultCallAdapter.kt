package com.br.diegocunha.pokedex.datasource.api

import java.lang.reflect.Type
import retrofit2.Call
import retrofit2.CallAdapter

class ResultCallAdapter(private val type: Type): CallAdapter<Type, Call<Result<Type>>> {

    override fun responseType(): Type = type

    override fun adapt(call: Call<Type>): Call<Result<Type>> = ResultCall(call)
}
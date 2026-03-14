package com.diegocunha.pokedex.datasource.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

fun createOkHttpClient(isDebug: Boolean): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(HeaderInterceptor())
        .apply {
            if (isDebug) {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
            }
        }
        .build()
}

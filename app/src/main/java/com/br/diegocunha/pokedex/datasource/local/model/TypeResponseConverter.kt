package com.br.diegocunha.pokedex.datasource.local.model

import com.google.gson.Gson

class TypeResponseConverter(
    private val gson: Gson
) {

//    @TypeConverter
//    fun fromString(value: String): List<PokemonInfo.TypeResponse>? {
//        val listType = TypeToken<List<PokemonInfo.TypeResponse>>(){}.type
//        return gson.fromJson(value, listType)
//    }
//
//    @TypeConverter
//    fun fromInfoType(type: List<PokemonInfo.TypeResponse>?): String {
//        return gson.toJson(type)
//    }
}
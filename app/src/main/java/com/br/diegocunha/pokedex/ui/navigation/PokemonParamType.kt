package com.br.diegocunha.pokedex.ui.navigation

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.br.diegocunha.pokedex.extensions.parcelableData
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

class PokemonParamType : NavType<PokemonParam>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): PokemonParam? {
        return bundle.parcelableData(key)
    }

    override fun parseValue(value: String): PokemonParam {
        return Gson().fromJson(value, PokemonParam::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: PokemonParam) {
        bundle.putParcelable(key, value)
    }
}

@Parcelize
data class PokemonParam(
    val name: String,
    val id: Int,
) : Parcelable
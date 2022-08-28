package com.br.diegocunha.pokedex.ui.navigation

import android.net.Uri
import com.google.gson.Gson

sealed class PokeScreen(val route: String) {
    object Home: PokeScreen("main")
    object PokemonDetail: PokeScreen("detail/{pokemonDetails}") {
        val argumentName = "pokemonDetails"

        fun navigate(param: PokemonParam): String {
            val parsedPokemon = Gson().toJson(param)
            return route.replace("{pokemonDetails}", Uri.encode(parsedPokemon))
        }
    }
}
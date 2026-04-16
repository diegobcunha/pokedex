// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kover)
}

dependencies {
    kover(project(":core"))
    kover(project(":datasource"))
    kover(project(":feature:pokemon"))
}

kover {
    reports {
        filters {
            excludes {
                packages(
                    // UI / Compose — no unit-testable logic
                    "com.diegocunha.pokedex.coreui",
                    "com.diegocunha.pokedex.feature.pokemon.presentation",
                    "com.diegocunha.pokedex.feature.evolutions.presentation",
                    // DI modules — wiring-only, no business logic
                    "com.diegocunha.pokedex.core.di",
                    "com.diegocunha.pokedex.datasource.di",
                    // DTOs — pure data holders, no business logic
                    "com.diegocunha.pokedex.datasource.model",
                    "com.diegocunha.pokedex.feature.pokemon.data.paging"
                )
                // All Composable-annotated functions
                annotatedBy("androidx.compose.runtime.Composable")
                classes(
                    // Android/Room/Compose generated code
                    "*.BuildConfig",
                    "*.ComposableSingletons*",
                    "*Database_Impl*",
                    "*Dao_Impl*",
                    // Feature DI modules
                    "com.diegocunha.pokedex.feature.pokemon.di.PokemonModuleKt",
                    "com.diegocunha.pokedex.feature.evolutions.di.EvolutionsModuleKt",
                    // Navigation graphs and route constants
                    "com.diegocunha.pokedex.feature.pokemon.navigation.PokemonNavGraphKt",
                    "com.diegocunha.pokedex.feature.evolutions.navigation.EvolutionNavGraphKt",
                    "com.diegocunha.pokedex.feature.pokemon.navigation.PokemonRoutes",
                    "com.diegocunha.pokedex.feature.evolutions.navigation.EvolutionRoutes",
                    // Android entry points — not unit-testable
                    "*.MainActivity",
                    "*.PokedexApplication",
                    // Network infrastructure factories
                    "*OkHttpClientFactoryKt",
                    "*RetrofitFactoryKt",
                    // Room abstract DB class
                    "*.PokedexDatabase",
                    // Coroutine dispatcher implementation
                    "*.DispatchersProviderImpl",
                )
            }
        }
        verify {
            rule {
                minBound(80)
            }
        }
    }
}

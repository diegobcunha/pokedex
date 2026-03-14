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
    kover(project(":feature:evolutions"))
}

kover {
    reports {
        filters {
            excludes {
                packages(
                    "com.diegocunha.pokedex.coreui",
                    "com.diegocunha.pokedex.feature.pokemon.presentation",
                    "com.diegocunha.pokedex.feature.evolutions.presentation"
                )
                annotatedBy("androidx.compose.runtime.Composable")
            }
        }
        verify {
            rule {
                minBound(80)
            }
        }
    }
}

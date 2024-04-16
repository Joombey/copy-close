package dev.farukh.copyclose.core

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed interface Screen {

    val route: String
    val args: List<NamedNavArgument> get() = emptyList()

    data object AuthGraph: Screen {
        override val route get() = "auth"

        data object Auth: Screen {
            override val route: String = "${AuthGraph.route}/login"
        }

        data object Register: Screen {
            override val route: String = "${AuthGraph.route}/register"
        }
    }
    data class MapScreen(val userID: String): Screen by Companion {
        val navRoute = "map/$userID"
        companion object: Screen {
            override val route: String = "map/{userID}"
            override val args: List<NamedNavArgument> = listOf(
                navArgument("userID") { type = NavType.StringType }
            )
        }
    }
}
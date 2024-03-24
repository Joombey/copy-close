package dev.farukh.copyclose

import androidx.navigation.NamedNavArgument

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
}
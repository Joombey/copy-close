package dev.farukh.copyclose

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavArgs

sealed interface Screen {
    val route: String
    val args: List<NamedNavArgument> get() = emptyList()

    data object AuthGraph: Screen {
        override val route get() = "auth"

        data object SingIn: Screen {
            override val route: String = "${AuthGraph.route}/sing-in"
        }

        data object SignUp: Screen {
            override val route: String = "${AuthGraph.route}/sing-up"
        }
    }
}
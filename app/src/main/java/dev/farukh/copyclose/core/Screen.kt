package dev.farukh.copyclose.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed interface Screen {

    val route: String
    val args: List<NamedNavArgument> get() = emptyList()
    val navIcon: ImageVector? get() = null

    data object Splash : Screen {
        override val route: String = "splash"
    }

    data object AuthGraph : Screen {
        override val route get() = "auth"

        data object Auth : Screen {
            override val route: String = "${AuthGraph.route}/login"
        }

        data object Register : Screen {
            override val route: String = "${AuthGraph.route}/register"
        }
    }

    data object Map : Screen {
        override val route: String = "map"
        override val navIcon: ImageVector = Icons.Filled.Map
    }

    class Profile(userID: String) : Screen by Companion {
        override val route: String = "profile/$userID"

        companion object : Screen {
            override val route: String = "profile/{id}"
            override val args: List<NamedNavArgument> = listOf(
                navArgument("id") { type = NavType.StringType }
            )
            override val navIcon: ImageVector = Icons.Filled.Person
        }
    }

    class Orders(userID: String) : Screen by Companion {
        override val route: String = "orders/$userID"

        companion object : Screen {
            override val route: String = "orders/{id}"
            override val args: List<NamedNavArgument> = listOf(
                navArgument("id") { type = NavType.StringType }
            )
            override val navIcon: ImageVector = Icons.Filled.ChecklistRtl
        }
    }

    class OrderCreation(userID: String) : Screen by Companion {
        override val route: String = "order-create/$userID"

        companion object : Screen {
            override val route: String = "order-create/{id}"
            override val args: List<NamedNavArgument> = listOf(
                navArgument("id") { type = NavType.StringType }
            )
        }
    }

    class Chat(userID: String, orderID: String): Screen by Companion {
        override val route: String = "chat/$orderID?userID=$userID"
        companion object: Screen {
            override val route: String = "chat/{orderID}?userID={userID}"
            override val args: List<NamedNavArgument> = listOf(
                navArgument(name = "orderID") { type = NavType.StringType },
                navArgument(name = "userID") { type = NavType.StringType }
            )
        }
    }

    data object Admin: Screen {
        override val route: String = "admin/"
        override val navIcon = Icons.Filled.AdminPanelSettings
    }
}
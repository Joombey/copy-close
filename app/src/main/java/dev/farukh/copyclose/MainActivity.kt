package dev.farukh.copyclose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import dev.farukh.copyclose.ui.theme.CopycloseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CopycloseTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) {
                    App(
                        padding = it,
                        Screen.AuthGraph.SingIn
                    )
                }
            }
        }
    }
}

@Composable
fun App(
    padding: PaddingValues,
    startScreen: Screen
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startScreen.route,
        modifier = Modifier.padding(padding).fillMaxSize()
    ) {
        authGraph()
    }
}

fun NavGraphBuilder.authGraph() {
    navigation(
        startDestination = Screen.AuthGraph.SignUp.route,
        route = Screen.AuthGraph.route
    ) {
        composable(
            route = Screen.AuthGraph.SingIn.route,
            arguments = Screen.AuthGraph.SingIn.args
        ) {

        }

        composable(
            route = Screen.AuthGraph.SignUp.route,
            arguments = Screen.AuthGraph.SignUp.args
        ) {

        }
    }
}
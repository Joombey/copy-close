package dev.farukh.copyclose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import dev.farukh.copyclose.features.auth.ui.compose.AuthScreen
import dev.farukh.copyclose.features.register.ui.compose.RegisterScreen
import dev.farukh.copyclose.ui.theme.CopycloseTheme
import dev.farukh.copyclose.utils.toast
import org.kodein.di.compose.rememberViewModel
import org.kodein.di.compose.withDI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            withDI(di = (application as CopyCloseApp).di) {
                CopycloseTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        App(
                            padding = it,
                            startScreen = Screen.AuthGraph
                        )
                    }
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
    val viewModel: MainViewModel by rememberViewModel()
    LaunchedEffect(Unit) {
        viewModel.currentScreen.collect {
            navController.navigate(it.route)
        }
    }
    NavHost(
        navController = navController,
        startDestination = startScreen.route,
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        authGraph(
            onLoginSuccess = {
//                navController.navigate(Screen.AuthGraph.Auth.route)
            },
            onRegisterPress = viewModel::toRegister,
            onRegisterSuccess = viewModel::toAuth,
        )
    }
}

fun NavGraphBuilder.authGraph(
    onLoginSuccess: () -> Unit,
    onRegisterPress: () -> Unit,
    onRegisterSuccess: () -> Unit,
) {
    navigation(
        startDestination = Screen.AuthGraph.Auth.route,
        route = Screen.AuthGraph.route
    ) {
        composable(
            route = Screen.AuthGraph.Auth.route,
            arguments = Screen.AuthGraph.Auth.args
        ) {
            AuthScreen(
                onLoginSuccess = onLoginSuccess,
                onRegister = onRegisterPress,
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(
            route = Screen.AuthGraph.Register.route,
            arguments = Screen.AuthGraph.Register.args
        ) {
//            Text("")
//            LocalContext.current.toast("Wow")
            RegisterScreen(
                onRegisterSuccess = onRegisterSuccess
            )
        }
    }
}

fun NavController.navigateWithOptionsTo(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateWithStateLoss(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = false
    }
}
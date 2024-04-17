package dev.farukh.copyclose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import dev.farukh.copyclose.core.Screen
import dev.farukh.copyclose.features.auth.ui.compose.AuthScreen
import dev.farukh.copyclose.features.map.compose.MapScreen
import dev.farukh.copyclose.features.register.ui.compose.RegisterScreen
import dev.farukh.copyclose.utils.UiUtils
import org.kodein.di.compose.rememberViewModel
import org.kodein.di.compose.withDI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            withDI(di = (application as CopyCloseApp).di) {
                MaterialTheme {
                    App()
                }
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
    val viewModel: MainViewModel by rememberViewModel()

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val activeUserID by viewModel.activeUser.collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(Unit) {
        viewModel.currentScreen.collect { nextScreen ->
            navController.navigateWithOptionsTo(nextScreen.route)
        }
    }

    Scaffold(
        bottomBar = {
            activeUserID?.let {
                MapBottomNav(
                    selectedScreen = currentScreen,
                    onMap = { viewModel::toMap },
                    onOrders = { viewModel.toOrders(it) },
                    onProfile = { viewModel.toProfile(it) }
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            authGraph(
                onLoginSuccess = viewModel::makeUserActive,
                onRegisterPress = viewModel::toRegister,
                onRegisterSuccess = viewModel::toAuth,
            )
            composable(
                route = Screen.Map.route,
                arguments = Screen.Map.args,
            ) { navBackStackEntry ->
                val userArg = navBackStackEntry.arguments!!.getString("userID")!!
                MapScreen()
            }

            composable(
                route = Screen.Splash.route,
                arguments = Screen.Splash.args
            ) {
                AppSplash()
            }
        }
    }
}

@Composable
fun AppSplash() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Text(text = stringResource(id = R.string.content_loading))
    }
}

fun NavGraphBuilder.authGraph(
    onLoginSuccess: (String) -> Unit,
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
            RegisterScreen(
                onRegisterSuccess = onRegisterSuccess,
                modifier = Modifier.padding(UiUtils.containerPaddingDefault)
            )
        }
    }
}

@Composable
fun MapBottomNav(
    selectedScreen: Screen,
    onMap: () -> Unit,
    onOrders: () -> Unit,
    onProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(modifier) {
        NavigationBarItem(
            selected = selectedScreen is Screen.Map,
            onClick = { if (selectedScreen !is Screen.Map) onMap() },
            icon = { Icon(Screen.Map.navIcon, null) },
            label = { Text(stringResource(id = R.string.map)) },
        )

        NavigationBarItem(
            selected = selectedScreen is Screen.Orders,
            onClick = { if (selectedScreen !is Screen.Orders) onOrders() },
            icon = { Icon(Screen.Orders.navIcon, null) },
            label = { Text(stringResource(id = R.string.orders)) }
        )

        NavigationBarItem(
            selected = selectedScreen is Screen.Profile,
            onClick = { if (selectedScreen !is Screen.Profile) onProfile() },
            icon = { Icon(Screen.Profile.navIcon, null) },
            label = { Text(stringResource(id = R.string.profile)) }
        )
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
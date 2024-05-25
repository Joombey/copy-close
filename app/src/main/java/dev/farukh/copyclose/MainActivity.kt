package dev.farukh.copyclose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.rememberCoroutineScope
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
import dev.farukh.copyclose.core.utils.UiUtils
import dev.farukh.copyclose.features.auth.ui.compose.AuthScreen
import dev.farukh.copyclose.features.map.ui.compose.MapScreen
import dev.farukh.copyclose.features.order_creation.ui.compose.OrderCreationScreen
import dev.farukh.copyclose.features.profile.ui.compose.ProfileScreen
import dev.farukh.copyclose.features.register.ui.compose.RegisterScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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
    val activeUserID by viewModel.activeUser.collectAsStateWithLifecycle(initialValue = null)
    val scope = rememberCoroutineScope()

    LaunchAuthCheck(
        navController = navController,
        source = viewModel.activeUser
    )

    Scaffold(
        bottomBar = {
            activeUserID?.let {
                MapBottomNav(
                    selectedScreen = Screen.Splash,
                    onMap = { navController.navigateWithStateAndSingle(Screen.Map.route) },
                    onOrders = { navController.navigateWithStateAndSingle(Screen.Orders(it).route) },
                    onProfile = {
                        val isProfile: Boolean = navController.currentDestination?.route?.contains("profile") == true
                        val profileId: String = navController.currentBackStackEntry?.arguments?.getString("userID") ?: ""
                        if (isProfile && profileId == it) {
                            navController.navigateWithStateAndSingle(Screen.Profile(it).route)
                        } else {
                            navController.navigateWithState(Screen.Profile(it).route)
                        }
                    }
                )
            }
        }
    ) { scaffoldPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier
                .padding(scaffoldPadding)
                .padding(UiUtils.containerPaddingDefault)
                .fillMaxSize()
        ) {
            authGraph(
                onLoginSuccess = { userID ->
                    scope.launch {
                        viewModel.makeUserActive(userID).join()
                        navController.navigateWithStateAndSingle(Screen.Map.route)
                    }
                },
                onRegisterPress = { navController.navigateWithStateAndSingle(Screen.AuthGraph.Register.route) },
                onRegisterSuccess = { navController.navigateWithStateAndSingle(Screen.AuthGraph.Auth.route) },
            )
            composable(
                route = Screen.Map.route,
                arguments = Screen.Map.args,
            ) {
                MapScreen(
                    onSellerClick = { sellerID ->
                        navController.navigateWithStateAndSingle(Screen.Profile(sellerID).route)
                    }
                )
            }

            composable(
                route = Screen.Orders.route,
                arguments = Screen.Orders.args
            ) { AppSplash() }

            composable(
                route = Screen.Profile.route,
                arguments = Screen.Profile.args
            ) { navBackStack ->
                val userID = navBackStack.arguments!!.getString("userID")!!
                ProfileScreen(
                    userID = userID,
                    onLogOut = { viewModel.logOut(activeUserID!!) },
                    onCreateOrder = {
                        navController.navigateWithStateAndSingle(Screen.OrderCreation(userID).route)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable(
                route = Screen.OrderCreation.route,
                arguments = Screen.OrderCreation.args
            ) { navBackStack ->
                val sellerID = navBackStack.arguments!!.getString("userID")!!
                OrderCreationScreen(
                    sellerID = sellerID,
                    onProfileClick = {
                        navController.navigateWithStateAndSingle(Screen.Profile(sellerID).route)
                    },
                    onOrderCreated = {
                        navController.navigateWithPopUp(
                            from = Screen.Map.route,
                            to = Screen.Orders(activeUserID!!).route
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            composable(
                route = Screen.Splash.route,
                arguments = Screen.Splash.args
            ) { AppSplash() }
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

@Composable
fun LaunchAuthCheck(
    navController: NavController,
    source: Flow<String?>
) {
    LaunchedEffect(Unit) {
        source.collect { userID ->
            if (userID == null && navController.currentBackStackEntry?.destination?.route?.contains(Screen.AuthGraph.route) == false) {
                navController.navigateWithStateAndSingle(Screen.AuthGraph.Auth.route)
            } else if (userID != null && navController.currentDestination?.route == Screen.Splash.route) {
                navController.navigateWithStateAndSingle(Screen.Map.route)
            }
        }
    }
}

fun NavController.navigateWithStateAndSingle(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateWithState(route: String) {
    navigate(route) {
        restoreState = true
    }
}

fun NavController.navigateWithPopUp(from: String, to: String) {
    navigate(to) {
        launchSingleTop = true
        popUpTo(from) {
            saveState = false
            inclusive = true
        }
    }
}
package dev.farukh.copyclose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import dev.farukh.copyclose.ui.theme.CopycloseTheme
import org.kodein.di.compose.rememberViewModel
import org.kodein.di.compose.withDI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            withDI(di = (application as CopyCloseApp).di) {
                val viewModel: MainViewModel by rememberViewModel()
                val list by viewModel.list.collectAsStateWithLifecycle(emptyList())
                CopycloseTheme {
                    Text(text = "3131321")
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier.padding(it),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(onClick = {
                                viewModel.newUser()
                            }) {
                                Text(text = "Create User")
                            }
                            for (en in list) {
                                Text(en.name, Modifier.clickable { viewModel.delete(en.id) })
                            }
                        }
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
    NavHost(
        navController = navController,
        startDestination = startScreen.route,
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
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
//            AuthScreen()
        }
    }
}
package dev.farukh.copyclose.features.auth.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.farukh.copyclose.R
import dev.farukh.copyclose.features.auth.authDI
import dev.farukh.copyclose.features.auth.ui.AuthErrors
import dev.farukh.copyclose.features.auth.ui.AuthViewModel
import dev.farukh.copyclose.utils.UiUtils
import dev.farukh.copyclose.utils.extensions.toast
import org.kodein.di.compose.localDI
import org.kodein.di.compose.rememberViewModel
import org.kodein.di.compose.withDI

@Composable
fun AuthScreen(
    onLoginSuccess: (String) -> Unit,
    onRegister: () -> Unit,
    modifier: Modifier = Modifier,
) = withDI(di = authDI(localDI())) {
    val viewModel: AuthViewModel by rememberViewModel()
    val context = LocalContext.current

    LaunchedEffect(key1 = viewModel.uiState.loggedIn) {
        viewModel.uiState.loggedIn?.let(onLoginSuccess)
    }

    LaunchedEffect(Unit) {
        for (err in viewModel.errChannel) {
            when(err) {
                AuthErrors.ErrorClient -> context.toast(R.string.err_client)
                AuthErrors.ErrorCredentials -> context.toast(R.string.err_creds)
                AuthErrors.ServerError -> context.toast(R.string.err_server)
            }
        }
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(value = viewModel.uiState.login, onValueChange = viewModel::setLogin)
        OutlinedTextField(
            value = viewModel.uiState.password,
            onValueChange = viewModel::setPassword
        )
        Button(
            onClick = viewModel::logIn
        ) {
            Text(stringResource(id = R.string.enter))
        }
        Button(
            onClick = onRegister
        ) {
            Text(stringResource(id = R.string.register))
        }
    }
}
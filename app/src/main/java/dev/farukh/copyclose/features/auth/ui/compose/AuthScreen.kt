package dev.farukh.copyclose.features.auth.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.toSize
import dev.farukh.copyclose.R
import dev.farukh.copyclose.core.utils.UiUtils
import dev.farukh.copyclose.core.utils.extensions.toast
import dev.farukh.copyclose.features.auth.authDI
import dev.farukh.copyclose.features.auth.ui.AuthErrors
import dev.farukh.copyclose.features.auth.ui.AuthViewModel
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
    var buttonSize by remember { mutableStateOf(Size.Zero) }

    LaunchedEffect(key1 = viewModel.uiState.loggedIn) {
        viewModel.uiState.loggedIn?.let(onLoginSuccess)
    }

    LaunchedEffect(Unit) {
        for (err in viewModel.errChannel) {
            when (err) {
                AuthErrors.ErrorClient -> context.toast(R.string.err_client)
                AuthErrors.ErrorCredentials -> context.toast(R.string.err_creds)
                AuthErrors.ServerError -> context.toast(R.string.err_server)
            }
        }
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.weight(3f)){
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.auth_label),
                    style = MaterialTheme.typography.headlineLarge,
                )
                OutlinedTextField(
                    value = viewModel.uiState.login,
                    onValueChange = viewModel::setLogin,
                    label = { Text(stringResource(id = R.string.login)) }
                )
                OutlinedTextField(
                    value = viewModel.uiState.password,
                    onValueChange = viewModel::setPassword,
                    label = { Text(text = stringResource(id = R.string.password)) },
                    modifier = Modifier.onGloballyPositioned { tfSize ->
                        buttonSize = tfSize.size.toSize()
                    }
                )
                OutlinedButton(
                    onClick = viewModel::logIn,
                    modifier = Modifier.width(
                        with(LocalDensity.current) {
                            buttonSize.width.toDp()
                        }
                    ),
                ) {
                    Text(stringResource(id = R.string.enter))
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        TextButton(
            onClick = onRegister,
            modifier = Modifier
                .width(
                    with(LocalDensity.current) {
                        buttonSize.width.toDp()
                    }
                ),
            colors = ButtonDefaults.textButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.primaryContainer,
            )
        ) {
            Text(stringResource(id = R.string.register))
        }
    }
}
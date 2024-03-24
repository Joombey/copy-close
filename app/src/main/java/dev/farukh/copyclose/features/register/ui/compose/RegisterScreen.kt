package dev.farukh.copyclose.features.register.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import dev.farukh.copyclose.R
import dev.farukh.copyclose.features.register.registerDI
import dev.farukh.copyclose.features.register.ui.RegisterViewModel
import dev.farukh.copyclose.utils.UiUtils
import org.kodein.di.compose.localDI
import org.kodein.di.compose.rememberViewModel
import org.kodein.di.compose.withDI

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    modifier: Modifier = Modifier,
) = withDI(di = registerDI(localDI())) {
    val viewModel: RegisterViewModel by rememberViewModel()
    LaunchedEffect(key1 = viewModel.uiState.registered) {
        if (viewModel.uiState.registered) onRegisterSuccess()
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault)
        ) {
            OutlinedTextField(
                value = viewModel.uiState.login,
                onValueChange = viewModel::setLogin,
                isError = viewModel.uiState.userExistsErr,
            )
            OutlinedTextField(
                value = viewModel.uiState.password,
                onValueChange = viewModel::setPassword,
                visualTransformation = PasswordVisualTransformation()
            )
            OutlinedTextField(
                value = viewModel.uiState.passwordConfirm,
                onValueChange = viewModel::setPasswordConfirm,
                visualTransformation = PasswordVisualTransformation()
            )
            OutlinedTextField(
                value = viewModel.uiState.query,
                onValueChange = viewModel::setQuery
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = UiUtils.contentPaddingDefault,
                verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault),
            ) {
                items(viewModel.uiState.addressList) {
                    Text(it.addressName)
                }
            }
        }

        Button(onClick = viewModel::register) {
            Text(text = stringResource(id = R.string.register))
        }
    }
}
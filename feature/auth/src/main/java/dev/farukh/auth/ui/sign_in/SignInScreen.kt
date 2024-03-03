package dev.farukh.auth.ui.sign_in

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.PanoramaFishEye
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.toSize
import dev.farukh.auth.R
import dev.farukh.utils.ui.UiDefaults

//TODO(): Сделать кастомную вёрстку этого экрана, на пока обычных кнопок достаточно
@Composable
fun SignInScreen(
    onSignInPress: () -> Unit,
    onSignUpPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    var login by rememberSaveable { mutableStateOf("123") }
    var password by rememberSaveable { mutableStateOf("321") }
    var passwordShown by rememberSaveable { mutableStateOf(false) }
    var buttonWidth by remember { mutableStateOf(Size.Zero) }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = UiDefaults.arrangementMedium,
                alignment = Alignment.Top
            )
        ) {
            OutlinedTextField(
                value = login,
                onValueChange = { login = it },
                leadingIcon = {
                    Icon(Icons.AutoMirrored.Filled.Login, null)
                },
                modifier = Modifier.onGloballyPositioned {
                    buttonWidth = it.size.toSize()
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                leadingIcon = {
                    Icon(Icons.Filled.Password, null)
                },
                trailingIcon = {
                    val icon = if (passwordShown) {
                        Icons.Filled.PanoramaFishEye
                    } else {
                        Icons.Filled.RemoveRedEye
                    }
                    IconButton(
                        onClick = {
                            passwordShown = !passwordShown
                        },
                        content = {
                            Icon(icon, null)
                        }
                    )
                },
                visualTransformation = if (!passwordShown) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
            )
            OutlinedButton(
                onClick = { },
                modifier = Modifier.width(
                    with(LocalDensity.current) {
                        buttonWidth.width.toDp()
                    }
                ),
                shape = RoundedCornerShape(10)
            ) {
                Text(stringResource(id = R.string.enter))
            }
        }
        Text(
            buildAnnotatedString {
                val noAccount = stringResource(id = R.string.no_account)
                val createAccount = stringResource(id = R.string.create_account)
                withStyle(SpanStyle(MaterialTheme.colorScheme.secondary)) {
                    append("$noAccount ")
                }
                val greenTextStyle = SpanStyle(
                    color = MaterialTheme.colorScheme.primary
                )
                withStyle(greenTextStyle) {
                    append(createAccount)
                }
            },
            modifier = Modifier.weight(1f, false)
        )
    }
}

@Preview(device = Devices.PIXEL_7_PRO, showSystemUi = true)
@PreviewLightDark
@Composable
fun SignInPreview() {
    MaterialTheme {
        SignInScreen(
            onSignInPress = {},
            onSignUpPress = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
package dev.farukh.auth.ui.sign_in

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.PanoramaFishEye
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.tooling.preview.UiMode
import androidx.compose.ui.unit.dp

//TODO(): Сделать кастомную вёрстку этого экрана, на пока обычных кнопок достаточно
@Composable
fun SignInView(
    onSignInPress: () -> Unit,
    onSignUpPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    var login by rememberSaveable { mutableStateOf("123") }
    var password by rememberSaveable { mutableStateOf("321") }
    var passwordShown by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.Top
        )
    ) {
        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            leadingIcon = {
                Icon(Icons.AutoMirrored.Filled.Login, null)
            },
            trailingIcon = {

            }
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
            }
        )
    }
}

@Preview(
    device = Devices.PIXEL_7_PRO,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
@PreviewLightDark
@Composable
fun SignInPreview() {
    MaterialTheme {
        SignInView(
            onSignInPress = {},
            onSignUpPress = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
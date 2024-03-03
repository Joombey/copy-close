package dev.farukh.auth.ui.sign_up

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.toSize
import dev.farukh.auth.R
import dev.farukh.utils.ui.UiDefaults
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun SignUpScreen(
    onSignUp: () -> Unit,
    onAddressChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    addressesSuggestions: ImmutableList<String> = persistentListOf(),
) {
    var login by remember { mutableStateOf("login") }
    var password by remember { mutableStateOf("password") }
    var name by remember { mutableStateOf("name") }
    var address by remember { mutableStateOf("address") }

    var buttonWidth by remember { mutableStateOf(Size.Zero) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(UiDefaults.arrangementMedium)
        ) {
            OutlinedTextField(
                value = login,
                onValueChange = { login = it },
                modifier = Modifier.onGloballyPositioned {
                    buttonWidth = it.size.toSize()
                }
            )
            OutlinedTextField(value = password, onValueChange = { password = it })
            OutlinedTextField(value = name, onValueChange = { name = it })
            OutlinedTextField(value = address, onValueChange = { address = it })
        }
        OutlinedButton(
            onClick = onSignUp,
            modifier = Modifier.width(
                with(LocalDensity.current) {
                    buttonWidth.width.toDp()
                }
            ),
            shape = UiDefaults.roundedDefault
        ) {
            Text(stringResource(id = R.string.create_account))
        }
    }
}


@Preview(
    device = Devices.PIXEL_7_PRO,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
@PreviewLightDark
@Composable
fun SignUpPreview() {
    SignUpScreen(
        onSignUp = {},
        onAddressChange = {},
        modifier = Modifier
            .padding(UiDefaults.containerPaddingMedium)
            .fillMaxSize()
    )
}
package dev.farukh.auth.ui.sign_up

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.farukh.core.di.ui.map.Map
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun AddressSearch(
    address: String,
    onAddressChange: (String) -> Unit,
    searchResult: ImmutableList<String>,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
        )

//        Map(
//            update = {
//
//            },
//            modifier = Modifier
//        )
    }
}

@Preview(
    device = Devices.PIXEL_7_PRO,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
@PreviewLightDark
@Composable
fun AddressSearchPreview() {
    AddressSearch(
        address = "321",
        onAddressChange = {},
        searchResult = persistentListOf("123")
    )
}
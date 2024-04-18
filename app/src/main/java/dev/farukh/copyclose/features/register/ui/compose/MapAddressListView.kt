package dev.farukh.copyclose.features.register.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.farukh.copyclose.features.register.data.model.Address
import dev.farukh.copyclose.utils.UiUtils
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MapAddressListView(
    addressList: ImmutableList<Address>,
    onAddress: (Address) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = UiUtils.contentPaddingDefault,
        verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(addressList) { address ->
            TextButton(
                onClick = { onAddress(address) }
            ) {
                Text(text = address.addressName)
            }
        }
    }
}
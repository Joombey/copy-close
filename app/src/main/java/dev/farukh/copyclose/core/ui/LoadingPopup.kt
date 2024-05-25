package dev.farukh.copyclose.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import dev.farukh.copyclose.R
import dev.farukh.copyclose.core.utils.UiUtils

@Composable
fun LoadingPopup(
    modifier: Modifier = Modifier,
    onDismissRequest: (() -> Unit)? = null,
    properties: PopupProperties = PopupProperties(),
) {
    Popup(
        alignment = Alignment.Center,
        properties = properties,
        onDismissRequest = onDismissRequest
    ){
        Column(
            modifier = modifier
                .clip(UiUtils.roundShapeDefault)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(UiUtils.containerPaddingDefault),
            verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Text(text = stringResource(R.string.content_loading))
        }
    }
}

@Composable
fun LoadingPopup(
    modifier: Modifier = Modifier,
    onDismissRequest: (() -> Unit)? = null,
    properties: PopupProperties = PopupProperties(),
    content: @Composable ColumnScope.() -> Unit
) {
    Popup(
        alignment = Alignment.Center,
        properties = properties,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = modifier
                .clip(UiUtils.roundShapeDefault)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(UiUtils.containerPaddingDefault),
            verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}

@Composable
fun LoadingDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    properties: DialogProperties = DialogProperties(),
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        properties = properties,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = modifier
                .clip(UiUtils.roundShapeDefault)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(UiUtils.containerPaddingDefault),
            verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}
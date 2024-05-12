package dev.farukh.copyclose.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Popup
import dev.farukh.copyclose.R
import dev.farukh.copyclose.core.utils.UiUtils

@Composable
fun LoadingPopup(modifier: Modifier = Modifier) {
    Popup(alignment = Alignment.Center) {
        Column(
            modifier = Modifier
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
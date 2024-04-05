package dev.farukh.copyclose.features.map.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.farukh.copyclose.features.map.mapDI
import org.kodein.di.compose.localDI
import org.kodein.di.compose.rememberViewModel
import org.kodein.di.compose.withDI

@Composable
fun MapScreen(
    userID: String
) = withDI(di = mapDI(localDI())) {
    val viewModel: MapViewModel by rememberViewModel()
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = userID)
    }
}
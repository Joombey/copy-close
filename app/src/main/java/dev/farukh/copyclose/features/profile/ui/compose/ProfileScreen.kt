package dev.farukh.copyclose.features.profile.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.farukh.copyclose.R
import dev.farukh.copyclose.core.utils.CircleImage
import dev.farukh.copyclose.core.utils.Toast
import dev.farukh.copyclose.core.utils.UiUtils
import dev.farukh.copyclose.features.profile.profileDI
import dev.farukh.copyclose.features.profile.ui.ProfileUIState
import dev.farukh.copyclose.features.profile.ui.ProfileViewModel
import org.kodein.di.compose.localDI
import org.kodein.di.compose.rememberViewModel
import org.kodein.di.compose.withDI

@Composable
fun ProfileScreen(
    userID: String,
    onLogOut: () -> Unit,
    modifier: Modifier = Modifier
) = withDI(di = profileDI(localDI())) {
    val viewModel: ProfileViewModel by rememberViewModel(tag = null, arg = userID)

    Box(modifier = Modifier.fillMaxSize()) {
        when (viewModel.uiState) {
            is ProfileUIState.ProfileData -> {
                ProfileView(
                    uiState = viewModel.uiState as ProfileUIState.ProfileData,
                    onLogOut = onLogOut,
                    modifier = modifier
                )
            }

            is ProfileUIState.Error -> {
                Toast(R.string.err_client)
                Button(
                    onClick = { viewModel.getUser(userID) },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(stringResource(R.string.err_retry))
                }
            }

            is ProfileUIState.Loading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun ProfileView(
    uiState: ProfileUIState.ProfileData,
    modifier: Modifier = Modifier,
    onLogOut: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        uiState.icon
            ?.let { image -> CircleImage(icon = image, size = UiUtils.imageSizeMedium) }
            ?: Box(modifier = Modifier
                .clip(CircleShape)
                .background(Color.Gray)
                .size(UiUtils.imageSizeMedium))

        Text(text = uiState.name)
        if (uiState.canEditProfile) {
            Button(onClick = onLogOut) {
                Text(text = stringResource(R.string.log_out))
            }
        }
    }
}
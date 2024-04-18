package dev.farukh.copyclose.features.profile.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.farukh.copyclose.R
import dev.farukh.copyclose.features.profile.profileDI
import dev.farukh.copyclose.features.profile.ui.ProfileViewModel
import org.kodein.di.compose.localDI
import org.kodein.di.compose.rememberViewModel
import org.kodein.di.compose.withDI

@Composable
fun ProfileScreen(
    isMe: Boolean,
    onLogOut: () -> Unit,
    modifier: Modifier = Modifier
) = withDI(di = profileDI(localDI())) {
    val viewModel: ProfileViewModel by rememberViewModel()

    Column(modifier) {
        if (isMe) {
            Button(
                onClick = onLogOut
            ) {
                Text(text = stringResource(id = R.string.log_out))
            }
        }
    }
}
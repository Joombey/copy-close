package dev.farukh.copyclose.features.profile.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.farukh.copyclose.R
import dev.farukh.copyclose.core.ui.LoadingPopup
import dev.farukh.copyclose.core.utils.CircleImage
import dev.farukh.copyclose.core.utils.Toast
import dev.farukh.copyclose.core.utils.UiUtils
import dev.farukh.copyclose.features.profile.profileDI
import dev.farukh.copyclose.features.profile.ui.ProfileActions
import dev.farukh.copyclose.features.profile.ui.ProfileUIState
import dev.farukh.copyclose.features.profile.ui.ProfileViewModel
import dev.farukh.copyclose.features.register.ui.compose.IconChooserView
import dev.farukh.network.core.ServiceCore
import org.kodein.di.compose.localDI
import org.kodein.di.compose.rememberViewModel
import org.kodein.di.compose.withDI

@Composable
fun ProfileScreen(
    userID: String,
    onLogOut: () -> Unit,
    onCreateOrder: () -> Unit,
    modifier: Modifier = Modifier
) = withDI(di = profileDI(localDI())) {
    val viewModel: ProfileViewModel by rememberViewModel(tag = null, arg = userID)

    Box(modifier = Modifier.fillMaxSize()) {
        when (viewModel.uiState) {
            is ProfileUIState.ProfileData -> {
                ProfileView(
                    uiState = viewModel.uiState as ProfileUIState.ProfileData,
                    onLogOut = onLogOut,
                    onCreateOrder = onCreateOrder,
                    profileActions = viewModel,
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
    profileActions: ProfileActions,
    onLogOut: () -> Unit,
    onCreateOrder: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        if (uiState.canEditProfile) {
            Row(
                modifier = Modifier.align(Alignment.TopEnd),
                horizontalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (uiState.editing) {
                            profileActions.saveChanges()
                        } else {
                            profileActions.startEdit()
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (uiState.editing) {
                            Icons.Filled.SaveAlt
                        } else {
                            Icons.Filled.Edit
                        },
                        contentDescription = null
                    )
                }
                IconButton(onClick = onLogOut) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null
                    )
                }
            }
        }

        if (uiState.updating) {
            LoadingPopup()
        }

        Column(
            modifier = Modifier.matchParentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            uiState.icon
                ?.let { image ->
                    if (uiState.editing) {
                        IconChooserView(
                            icon = image,
                            onChoose = profileActions::setIcon,
                            modifier = Modifier.size(UiUtils.imageSizeMedium),
                        )
                    } else {
                        CircleImage(icon = image, size = UiUtils.imageSizeMedium)
                    }
                }
                ?: Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .size(UiUtils.imageSizeMedium)
                )

            if (uiState.editing) {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = profileActions::setName
                )
            } else {
                Text(text = uiState.name)
            }

            if (uiState.isSeller) {
                if (uiState.editing) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.service_profile_title))
                        IconButton(onClick = profileActions::addService) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                        }
                    }
                }

                if (!uiState.canEditProfile) {
                    Button(onClick = onCreateOrder) {
                        Text(text = stringResource(id = R.string.create_order))
                    }
                }

                if (uiState.services.isNotEmpty()) {
                    ServicesListView(
                        services = uiState.services,
                        editing = uiState.editing,
                        onTitleChange = profileActions::setTitle,
                        onPriceChange = profileActions::setPrice,
                        onRemove = profileActions::removeServiceAt,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = stringResource(R.string.no_services))
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ServicesListView(
    services: List<ServiceCore>,
    editing: Boolean,
    onTitleChange: (Int, String) -> Unit,
    onPriceChange: (Int, Int) -> Unit,
    onRemove: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(items = services, key = { index, _ -> index }) { index, service ->
            Row(
                modifier = Modifier.fillParentMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (editing) {
                    OutlinedTextField(
                        value = service.title,
                        onValueChange = { onTitleChange(index, it) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        placeholder = { Text(text = service.title) },
                        modifier = Modifier.weight(2f)
                    )

                    OutlinedTextField(
                        value = "${service.price}",
                        onValueChange = { onPriceChange(index, it.toIntOrNull() ?: 0) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        placeholder = {
                            Text(
                                text = stringResource(
                                    R.string.price,
                                    service.price
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = { onRemove(index) }
                    ) {
                        Icon(imageVector = Icons.Filled.Remove, contentDescription = null)
                    }
                } else {
                    Text(text = service.title)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = stringResource(R.string.price, service.price))
                }
            }
        }
    }
}

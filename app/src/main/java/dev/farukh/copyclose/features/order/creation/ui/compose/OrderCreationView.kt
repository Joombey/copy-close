package dev.farukh.copyclose.features.order.creation.ui.compose

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.DialogProperties
import dev.farukh.copyclose.R
import dev.farukh.copyclose.core.data.models.MediaInfo
import dev.farukh.copyclose.core.ui.InfoDialog
import dev.farukh.copyclose.core.utils.UiUtils
import dev.farukh.copyclose.features.order.creation.ui.CreationUIState
import dev.farukh.copyclose.features.order.creation.ui.OrderCreationActions
import dev.farukh.copyclose.features.order.creation.ui.OrderCreationUIState
import dev.farukh.copyclose.ui.theme.DOCXBackground
import dev.farukh.copyclose.ui.theme.OtherExtensionBackground
import dev.farukh.copyclose.ui.theme.PDFBackground
import dev.farukh.network.core.ServiceCore

@Composable
fun OrderCreationView(
    uiState: OrderCreationUIState.OrderCreationData,
    actions: OrderCreationActions,
    onProfileClick: () -> Unit,
    onOrderCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault)
    ) {
        OrderCreationProgressDialog(
            creationState = uiState.creationState,
            onOrderCreated = onOrderCreated,
            onCreationInfoConfirmed = actions::creationInfoConfirmed,
            modifier = Modifier.sizeIn(
                minWidth = 200.dp,
                maxWidth = 400.dp,
                minHeight = 100.dp,
                maxHeight = 300.dp
            ),
        )
        UserHeaderView(
            userIcon = uiState.icon,
            userName = uiState.name,
            userAddress = uiState.address,
            onIconClick = onProfileClick,
            modifier = Modifier.fillMaxWidth(),
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .height(UiUtils.dividerThicknessDefault),
            thickness = UiUtils.dividerThicknessDefault,
            color = Color.Black
        )
        OrdersView(
            services = uiState.services,
            onRemove = actions::removeAmount,
            onAdd = actions::addAmount,
            modifier = Modifier.fillMaxWidth()
        )
        AttachFileView(
            onFileChosen = actions::attachFile,
            onFileRemove = actions::detachFile,
            attachedFiles = uiState.attachedFiles,
            modifier = Modifier.height(100.dp)
        )
        OutlinedTextField(
            value = uiState.comment,
            onValueChange = actions::changeComment,
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Comment,
                    contentDescription = null
                )
            },
            label = {
                Text(
                    text = stringResource(id = R.string.comment_label),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .height(UiUtils.dividerThicknessDefault),
            thickness = UiUtils.dividerThicknessDefault,
            color = Color.Black
        )
        TotalPriceView(
            price = uiState.services.sumOf { it.first.price * it.second },
            modifier = Modifier.fillMaxWidth()
        )
        if (uiState.canOrder) {
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = actions::create,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.create_order),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun OrderCreationProgressDialog(
    creationState: CreationUIState,
    onOrderCreated: () -> Unit,
    onCreationInfoConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (creationState !is CreationUIState.Idle) {
        InfoDialog(
            modifier = modifier,
            properties = DialogProperties(dismissOnClickOutside = creationState.canDismiss),
            onDismissRequest = { if (creationState.canDismiss) onCreationInfoConfirmed() }
        ) {
            when (creationState) {
                is CreationUIState.ErrorCreation -> {
                    Text(text = stringResource(id = R.string.create_order_err))
                    Button(onClick = onCreationInfoConfirmed) {
                        Text(text = stringResource(id = android.R.string.ok))
                    }
                }

                is CreationUIState.SendingInfo -> {
                    Text(text = stringResource(id = R.string.create_order_sending_info))
                    LinearProgressIndicator()
                }

                is CreationUIState.StartSending -> {
                    Text(text = stringResource(id = R.string.create_order_start_sending))
                    LinearProgressIndicator()
                }

                is CreationUIState.Success -> {
                    Text(text = stringResource(id = R.string.create_order_success))
                    Button(onClick = onOrderCreated) {
                        Text(text = stringResource(id = android.R.string.ok))
                    }
                }

                is CreationUIState.UploadingFile -> {
                    Text(text = stringResource(id = R.string.create_order_uploading_documents))
                    LinearProgressIndicator(
                        progress = { creationState.progress },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
fun TotalPriceView(
    price: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.total),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(id = R.string.price, price),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun UserHeaderView(
    userIcon: ImageBitmap,
    userName: String,
    userAddress: String,
    onIconClick: (() -> Unit),
    modifier: Modifier = Modifier,
) {
    var containerSize by remember { mutableStateOf(Size.Zero) }
    val state = rememberScrollState()
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault)
    ) {
        Image(
            bitmap = userIcon,
            contentDescription = null,
            modifier = Modifier
                .border(UiUtils.borderWidthDefault, Color.Black, CircleShape)
                .clip(CircleShape)
                .padding(UiUtils.borderWidthDefault)
                .size(with(LocalDensity.current) { containerSize.height.toDp() })
                .clickable(onClick = onIconClick),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .onGloballyPositioned { containerSize = it.size.toSize() },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = userAddress,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.horizontalScroll(state),
                maxLines = 1,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Visible
            )
        }
    }
}

@Composable
fun AttachFileView(
    onFileChosen: (Uri) -> Unit,
    onFileRemove: (Int) -> Unit,
    attachedFiles: List<MediaInfo>,
    modifier: Modifier = Modifier,
) {
    val openFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null || attachedFiles.any { it.uri == uri }) {
            return@rememberLauncherForActivityResult
        } else {
            onFileChosen(uri)
        }
    }
    val measurer = rememberTextMeasurer()
    var containerSize by remember {
        mutableStateOf(Size.Zero)
    }
    if (attachedFiles.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .onGloballyPositioned { containerSize = it.size.toSize() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {
                    openFileLauncher.launch(
                        arrayOf(
                            "application/pdf",
                            "application/msword",
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                        )
                    )
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .size(
                        with(LocalDensity.current) {
                            val labelSize =
                                measurer.measure(stringResource(id = R.string.attach_file))
                            val labelDpHeight = labelSize.size.toSize().height.toDp()
                            containerSize.height.toDp() - labelDpHeight
                        }
                    )
            ) {
                Icon(imageVector = Icons.Filled.AttachFile, contentDescription = null)
            }
            Text(
                text = stringResource(id = R.string.attach_file),
                style = MaterialTheme.typography.labelMedium
            )
        }
    } else {
        LazyRow(
            modifier = modifier.onGloballyPositioned {
                containerSize = it.size.toSize()
            },
            horizontalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault),
            contentPadding = UiUtils.contentPaddingDefault
        ) {
            item {
                Column(
                    modifier = modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = {
                            openFileLauncher.launch(
                                arrayOf(
                                    "application/pdf",
                                    "application/msword",
                                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                                )
                            )
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(
                                with(LocalDensity.current) {
                                    val labelSize =
                                        measurer.measure(stringResource(id = R.string.attach_file))
                                    val labelDpHeight = labelSize.size.toSize().height.toDp()
                                    containerSize.height.toDp() - labelDpHeight - UiUtils.arrangementDefault
                                }
                            )
                    ) {
                        Icon(imageVector = Icons.Filled.AttachFile, contentDescription = null)
                    }
                    Text(
                        text = stringResource(id = R.string.attach_file),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            itemsIndexed(
                items = attachedFiles,
                key = { index, _ -> index }
            ) { index, item ->
                AttachedFile(
                    name = item.name,
                    extension = item.extensions,
                    size = item.size.toInt(),
                    onDelete = { onFileRemove(index) },
                    modifier = Modifier
                        .size(height = 150.dp, width = 150.dp)
                        .padding(UiUtils.containerPaddingDefault)
                )
            }
        }
    }
}

@Composable
fun OrdersView(
    services: List<Pair<ServiceCore, Int>>,
    onRemove: (Int) -> Unit,
    onAdd: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        itemsIndexed(
            items = services,
            key = { index, _ -> index },
        ) { index, item ->
            val (service, amount) = item
            Row(
                modifier = Modifier.fillParentMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = service.title)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(id = R.string.price, service.price))
                    IconButton(onClick = { onRemove(index) }) {
                        Icon(imageVector = Icons.Filled.Remove, contentDescription = null)
                    }
                    Text(amount.toString())
                    IconButton(onClick = { onAdd(index) }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun AttachedFile(
    extension: String,
    name: String,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null,
    size: Int? = null,
) {
    Column(
        modifier = Modifier
            .clip(UiUtils.roundShapeDefault)
            .background(
                when (extension) {
                    "pdf" -> PDFBackground
                    "docx", "doc" -> DOCXBackground
                    else -> OtherExtensionBackground
                }
            )
            .then(modifier)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall
            )
            if (onDelete != null) {
                IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                    )
                }
            }
        }
        Text(
            text = extension.uppercase(),
            style = MaterialTheme.typography.titleSmall.copy(
                color = when (extension) {
                    "pdf" -> Color.White
                    "docx", "doc" -> Color.Black
                    else -> Color.White
                },
            ),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        if (size != null) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(
                    id = R.string.media_size,
                    size.toFloat() / 1024 / 1024
                ),
                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic)
            )
        }
    }
}
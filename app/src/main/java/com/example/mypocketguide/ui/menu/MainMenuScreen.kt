package com.example.mypocketguide.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mypocketguide.MyPocketGuideApp
import com.example.mypocketguide.R
import com.example.mypocketguide.domain.Exhibit
import com.example.mypocketguide.ui.theme.*

@Composable
fun MainMenuScreen(
    onOpenExhibit: (Exhibit) -> Unit = {},
    onOpenScan: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val app = context.applicationContext as MyPocketGuideApp
    val viewModel: MainMenuViewModel = viewModel(
        factory = MainMenuViewModelFactory(app.container.historyRepository)
    )
    val history by viewModel.history.collectAsStateWithLifecycle()

    var showClearDialog by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item { MenuHeader(onOpenSettings = onOpenSettings) }
            item { ScanCtaCard(onClick = onOpenScan) }
            item {
                SectionTitle(
                    title = stringResource(R.string.menu_section_history),
                    actionText = if (history.isNotEmpty())
                        stringResource(R.string.menu_action_clear) else null,
                    onAction = { showClearDialog = true }
                )
            }

            if (history.isEmpty()) {
                item { EmptyHistory() }
            } else {
                items(history, key = { it.id }) { exhibit ->
                    MiniExhibitCard(exhibit = exhibit) { onOpenExhibit(exhibit) }
                }
            }
        }

        if (showClearDialog) {
            ClearHistoryDialog(
                onConfirm = {
                    viewModel.clearHistory()
                    showClearDialog = false
                },
                onDismiss = { showClearDialog = false }
            )
        }
    }
}

@Composable
private fun ScanCtaCard(onClick: () -> Unit) {
    val description = stringResource(R.string.scan_cta_description)

    Surface(
        color = Ink,
        shape = RoundedCornerShape(20.dp),
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .heightIn(min = 140.dp)
            // clearAndSetSemantics — стирает семантику ВСЕХ дочерних
            // элементов и оставляет только наше описание.
            // Без этого TalkBack прочитает три раза: описание + название + подзаголовок.
            .clearAndSetSemantics { contentDescription = description }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.PhotoCamera,
                contentDescription = null,
                tint = White,
                modifier = Modifier.size(56.dp)
            )
            Spacer(Modifier.width(20.dp))
            Column {
                Text(
                    stringResource(R.string.scan_cta_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = White
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    stringResource(R.string.scan_cta_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
private fun MenuHeader(onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(R.string.menu_header_title),
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                stringResource(R.string.menu_greeting_morning),
                style = MaterialTheme.typography.labelLarge,
                color = Gray
            )
        }
        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                Icons.Outlined.Settings,
                contentDescription = stringResource(R.string.settings_open),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    actionText: String? = null,
    onAction: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            // heading() — помечает текст как заголовок.
            // Пользователь TalkBack может листать «по заголовкам».
            modifier = Modifier
                .weight(1f)
                .semantics { heading() }
        )
        if (actionText != null) {
            val clearA11y = stringResource(R.string.menu_clear_history_a11y)
            TextButton(
                onClick = onAction,
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .clearAndSetSemantics { contentDescription = clearA11y }
            ) {
                Text(
                    actionText,
                    color = Blue,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun EmptyHistory() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "🏛",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.semantics { invisibleToUser() }
        )
        Spacer(Modifier.height(12.dp))
        Text(
            stringResource(R.string.history_empty_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            stringResource(R.string.history_empty_subtitle),
            style = MaterialTheme.typography.labelLarge,
            color = Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MiniExhibitCard(exhibit: Exhibit, onClick: () -> Unit) {
    Surface(
        color = White,
        shape = RoundedCornerShape(14.dp),
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            // mergeDescendants = true — TalkBack читает карточку ОДНИМ
            // элементом, а не названием, потом описанием, потом кнопкой.
            .semantics(mergeDescendants = true) {}
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExhibitThumbnail(
                imageBytes = exhibit.imageBytes,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    exhibit.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    exhibit.description,
                    style = MaterialTheme.typography.labelMedium,
                    color = Gray,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ExhibitThumbnail(imageBytes: ByteArray?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(Ghost),
        contentAlignment = Alignment.Center
    ) {
        if (imageBytes != null) {
            AsyncImage(
                model = imageBytes,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                "🏛",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.semantics { invisibleToUser() }
            )
        }
    }
}

@Composable
private fun ClearHistoryDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.clear_dialog_title)) },
        text = { Text(stringResource(R.string.clear_dialog_message)) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text(
                    stringResource(R.string.clear_dialog_confirm),
                    color = Red,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text(
                    stringResource(R.string.clear_dialog_cancel),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}
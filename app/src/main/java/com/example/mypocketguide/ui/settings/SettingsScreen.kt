package com.example.mypocketguide.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mypocketguide.BuildConfig
import com.example.mypocketguide.MyPocketGuideApp
import com.example.mypocketguide.R
import com.example.mypocketguide.data.settings.SettingsRepository
import com.example.mypocketguide.ui.common.PrimaryButton
import com.example.mypocketguide.ui.common.SecondaryButton
import com.example.mypocketguide.ui.theme.Gray
import com.example.mypocketguide.ui.theme.Green
import com.example.mypocketguide.ui.theme.Red

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as MyPocketGuideApp
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(app.container.settingsRepository)
    )

    val autoRead by viewModel.autoReadEnabled.collectAsStateWithLifecycle()
    val speechRate by viewModel.speechRate.collectAsStateWithLifecycle()
    val savedServerUrl by viewModel.serverUrl.collectAsStateWithLifecycle()
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()

    var urlDraft by remember { mutableStateOf("") }
    var urlInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(savedServerUrl) {
        if (!urlInitialized && savedServerUrl.isNotBlank()) {
            urlDraft = savedServerUrl
            urlInitialized = true
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.settings_back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // === ДОСТУПНОСТЬ ===
            SettingsSectionHeader(stringResource(R.string.settings_section_accessibility))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.settings_auto_read),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        stringResource(R.string.settings_auto_read_description),
                        style = MaterialTheme.typography.labelLarge,
                        color = Gray
                    )
                }
                Spacer(Modifier.width(12.dp))
                Switch(
                    checked = autoRead,
                    onCheckedChange = { viewModel.setAutoRead(it) }
                )
            }

            Spacer(Modifier.height(12.dp))

            // Слайдер скорости — с текущим значением над ним
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.settings_speech_rate),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "%.2fx".format(speechRate),
                    style = MaterialTheme.typography.labelLarge,
                    color = Gray
                )
            }
            Spacer(Modifier.height(8.dp))
            Slider(
                value = speechRate,
                onValueChange = { viewModel.setSpeechRate(it) },
                valueRange = SettingsRepository.MIN_SPEECH_RATE..SettingsRepository.MAX_SPEECH_RATE,
                steps = 4
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.settings_speech_rate_slow),
                    style = MaterialTheme.typography.labelLarge,
                    color = Gray
                )
                Text(
                    stringResource(R.string.settings_speech_rate_fast),
                    style = MaterialTheme.typography.labelLarge,
                    color = Gray
                )
            }

            Spacer(Modifier.height(28.dp))

            // === СЕРВЕР ===
            SettingsSectionHeader(stringResource(R.string.settings_section_server))

            OutlinedTextField(
                value = urlDraft,
                onValueChange = {
                    urlDraft = it
                    viewModel.resetConnectionState()
                },
                label = { Text(stringResource(R.string.settings_server_url)) },
                placeholder = { Text(stringResource(R.string.settings_server_url_hint)) },
                supportingText = {
                    Text(
                        stringResource(R.string.settings_server_url_helper),
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.settings_check_connection),
                    modifier = Modifier.weight(1f),
                    enabled = connectionState != ConnectionState.Checking,
                    onClick = { viewModel.checkConnection(urlDraft) }
                )
                PrimaryButton(
                    text = stringResource(R.string.settings_save),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.saveServerUrl(urlDraft) }
                )
            }

            Spacer(Modifier.height(12.dp))

            ConnectionStatus(state = connectionState)

            Spacer(Modifier.height(28.dp))

            // === О ПРИЛОЖЕНИИ ===
            SettingsSectionHeader(stringResource(R.string.settings_section_about))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.settings_version),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    BuildConfig.VERSION_NAME,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Gray
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(text: String) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = Gray,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
    )
}

@Composable
private fun ConnectionStatus(state: ConnectionState) {
    // liveRegion = Polite — TalkBack объявит об изменении статуса
    // ПОСЛЕ того, как закончит читать текущий элемент.
    Box(
        Modifier.semantics { liveRegion = LiveRegionMode.Polite }
    ) {
        when (state) {
            ConnectionState.Idle -> Unit
            ConnectionState.Checking -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        stringResource(R.string.settings_check_connection) + "…",
                        style = MaterialTheme.typography.labelLarge,
                        color = Gray
                    )
                }
            }
            ConnectionState.Success -> StatusLine(
                icon = Icons.Filled.CheckCircle,
                color = Green,
                text = stringResource(R.string.settings_check_success)
            )
            ConnectionState.Failure -> StatusLine(
                icon = Icons.Filled.Error,
                color = Red,
                text = stringResource(R.string.settings_check_failure)
            )
            ConnectionState.InvalidUrl -> StatusLine(
                icon = Icons.Filled.Error,
                color = Red,
                text = stringResource(R.string.settings_check_invalid_url)
            )
        }
    }
}

@Composable
private fun StatusLine(
    icon: ImageVector,
    color: Color,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.labelLarge,
            color = color
        )
    }
}
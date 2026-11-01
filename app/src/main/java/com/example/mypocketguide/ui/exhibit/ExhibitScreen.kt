package com.example.mypocketguide.ui.exhibit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mypocketguide.MyPocketGuideApp
import com.example.mypocketguide.R
import com.example.mypocketguide.domain.Exhibit
import com.example.mypocketguide.ui.common.PrimaryButton
import com.example.mypocketguide.ui.common.SecondaryButton
import com.example.mypocketguide.ui.common.rememberSpeaker
import com.example.mypocketguide.ui.theme.Ghost
import com.example.mypocketguide.ui.theme.Red
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExhibitScreen(
    exhibit: Exhibit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as MyPocketGuideApp
    val viewModel: ExhibitViewModel = viewModel(
        factory = ExhibitViewModelFactory(
            exhibit = exhibit,
            favoritesRepository = app.container.favoritesRepository,
            settingsRepository = app.container.settingsRepository
        )
    )
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val autoReadEnabled by viewModel.autoReadEnabled.collectAsStateWithLifecycle()
    val speechRate by viewModel.speechRate.collectAsStateWithLifecycle()

    val speaker = rememberSpeaker()
    val isReady by speaker.isReady.collectAsStateWithLifecycle()
    val isSpeaking by speaker.isSpeaking.collectAsStateWithLifecycle()
    val isLanguageSupported by speaker.isLanguageSupported.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val ttsUnavailableMessage = stringResource(R.string.exhibit_tts_not_available)

    val spokenText = remember(exhibit.id) {
        "${exhibit.title}. ${exhibit.description}"
    }

    // Применяем скорость речи из настроек
    LaunchedEffect(speechRate) {
        speaker.setSpeechRate(speechRate)
    }

    // Автозапуск озвучки — только если включено, язык поддержан и ещё не читали
    var autoReadDone by remember(exhibit.id) { mutableStateOf(false) }
    LaunchedEffect(exhibit.id, isReady, autoReadEnabled, isLanguageSupported) {
        if (autoReadEnabled && isReady && isLanguageSupported && !autoReadDone) {
            autoReadDone = true
            delay(300)
            speaker.speak(spokenText)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.exhibit_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back)
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
            ExhibitHero(imageBytes = exhibit.imageBytes)
            Spacer(Modifier.height(20.dp))

            Text(exhibit.title, style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(16.dp))
            Text(exhibit.description, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(28.dp))

            // Кнопка озвучки
            if (isSpeaking) {
                SecondaryButton(
                    text = stringResource(R.string.exhibit_action_stop),
                    icon = Icons.Filled.Stop,
                    borderColor = Red,
                    contentColor = Red,
                    onClick = { speaker.stop() }
                )
            } else {
                PrimaryButton(
                    text = stringResource(R.string.exhibit_action_listen),
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    onClick = {
                        if (isLanguageSupported) {
                            speaker.speak(spokenText)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(ttsUnavailableMessage)
                            }
                        }
                    }
                )
            }

            Spacer(Modifier.height(12.dp))
            SecondaryButton(
                text = stringResource(R.string.exhibit_action_scan_again),
                icon = Icons.Outlined.PhotoCamera,
                onClick = {}
            )
            Spacer(Modifier.height(12.dp))

            if (isFavorite) {
                SecondaryButton(
                    text = stringResource(R.string.exhibit_action_remove_from_favorite),
                    icon = Icons.Filled.Favorite,
                    iconTint = Red,
                    borderColor = Red,
                    onClick = { viewModel.toggleFavorite() }
                )
            } else {
                PrimaryButton(
                    text = stringResource(R.string.exhibit_action_favorite),
                    icon = Icons.Outlined.FavoriteBorder,
                    onClick = { viewModel.toggleFavorite() }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ExhibitHero(imageBytes: ByteArray?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(6f / 5f)
            .clip(RoundedCornerShape(16.dp))
            .background(Ghost),
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
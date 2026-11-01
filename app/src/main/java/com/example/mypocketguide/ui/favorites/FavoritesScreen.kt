package com.example.mypocketguide.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.example.mypocketguide.ui.theme.Ghost
import com.example.mypocketguide.ui.theme.Gray
import com.example.mypocketguide.ui.theme.White

@Composable
fun FavoritesScreen(
    onOpenExhibit: (Exhibit) -> Unit = {}
) {
    val context = LocalContext.current
    val app = context.applicationContext as MyPocketGuideApp
    val viewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(app.container.favoritesRepository)
    )
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        if (favorites.isEmpty()) {
            EmptyFavorites()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                item {
                    Text(
                        stringResource(R.string.favorites_title),
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }
                items(favorites, key = { it.id }) { exhibit ->
                    FavoriteCard(exhibit = exhibit) { onOpenExhibit(exhibit) }
                }
            }
        }
    }
}

@Composable
private fun EmptyFavorites() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = Gray
        )
        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(R.string.favorites_empty_title),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.favorites_empty_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FavoriteCard(exhibit: Exhibit, onClick: () -> Unit) {
    Surface(
        color = White,
        shape = RoundedCornerShape(14.dp),
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .semantics(mergeDescendants = true) {}
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Ghost),
                contentAlignment = Alignment.Center
            ) {
                if (exhibit.imageBytes != null) {
                    AsyncImage(
                        model = exhibit.imageBytes,
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
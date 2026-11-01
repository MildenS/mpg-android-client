package com.example.mypocketguide.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mypocketguide.R

enum class AppDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    Menu("menu", R.string.nav_home, Icons.Outlined.Home),
    Favorites("favorites", R.string.nav_favorites, Icons.Outlined.FavoriteBorder);

    companion object {
        val bottomBarTabs: List<AppDestination> = listOf(Menu, Favorites)

        fun fromRoute(route: String?): AppDestination? =
            values().firstOrNull { it.route == route }
    }
}
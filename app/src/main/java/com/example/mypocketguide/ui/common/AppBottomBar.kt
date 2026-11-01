package com.example.mypocketguide.ui.common

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.mypocketguide.ui.navigation.AppDestination
import com.example.mypocketguide.ui.theme.Ink
import com.example.mypocketguide.ui.theme.White

@Composable
fun AppBottomBar(
    destinations: List<AppDestination>,
    currentRoute: String?,
    onSelect: (AppDestination) -> Unit
) {
    NavigationBar(containerColor = White) {
        destinations.forEach { destination ->
            val selected = currentRoute == destination.route
            val label = stringResource(destination.labelRes)

            NavigationBarItem(
                selected = selected,
                onClick = { onSelect(destination) },
                icon = { Icon(destination.icon, contentDescription = label) },
                label = {
                    Text(label, style = MaterialTheme.typography.labelMedium)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Ink,
                    selectedTextColor = Ink,
                    indicatorColor = Ink.copy(alpha = 0.08f)
                )
            )
        }
    }
}
package com.example.mypocketguide.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mypocketguide.MyPocketGuideApp
import com.example.mypocketguide.domain.Exhibit
import com.example.mypocketguide.ui.common.AppBottomBar
import com.example.mypocketguide.ui.exhibit.ExhibitScreen
import com.example.mypocketguide.ui.favorites.FavoritesScreen
import com.example.mypocketguide.ui.menu.MainMenuScreen
import com.example.mypocketguide.ui.navigation.AppDestination
import com.example.mypocketguide.ui.navigation.Routes
import com.example.mypocketguide.ui.onboarding.AutoReadPromptDialog
import com.example.mypocketguide.ui.onboarding.OnboardingViewModel
import com.example.mypocketguide.ui.onboarding.OnboardingViewModelFactory
import com.example.mypocketguide.ui.scan.ScanScreen
import com.example.mypocketguide.ui.theme.Background
import com.example.mypocketguide.ui.settings.SettingsScreen

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    var selectedExhibit by remember { mutableStateOf<Exhibit?>(null) }

    val showBottomBar = AppDestination.bottomBarTabs.any { it.route == currentRoute }

    val context = LocalContext.current
    val app = context.applicationContext as MyPocketGuideApp
    val onboardingViewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModelFactory(app.container.settingsRepository)
    )
    val hasSeenPrompt by onboardingViewModel.hasSeenPrompt.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    destinations = AppDestination.bottomBarTabs,
                    currentRoute = currentRoute,
                    onSelect = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Menu.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(AppDestination.Menu.route) {
                MainMenuScreen(
                    onOpenExhibit = { exhibit ->
                        selectedExhibit = exhibit
                        navController.navigate(Routes.EXHIBIT)
                    },
                    onOpenScan = { navController.navigate(Routes.SCAN) },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) }
                )
            }
            composable(AppDestination.Favorites.route) {
                FavoritesScreen(
                    onOpenExhibit = { exhibit ->
                        selectedExhibit = exhibit
                        navController.navigate(Routes.EXHIBIT)
                    }
                )
            }
            composable(Routes.SCAN) {
                ScanScreen(
                    onExhibitRecognized = { exhibit ->
                        selectedExhibit = exhibit
                        navController.navigate(Routes.EXHIBIT) {
                            popUpTo(Routes.SCAN) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.EXHIBIT) {
                val exhibit = selectedExhibit
                if (exhibit != null) {
                    ExhibitScreen(
                        exhibit = exhibit,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }

    if (hasSeenPrompt == false) {
        AutoReadPromptDialog(
            onEnable = { onboardingViewModel.enableAutoRead() },
            onDecline = { onboardingViewModel.declineAutoRead() }
        )
    }
}
package com.breaswl.spexerutil.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.breaswl.spexerutil.ui.screens.HomeScreen
import com.breaswl.spexerutil.ui.screens.ModesScreen
import com.breaswl.spexerutil.ui.screens.SettingsScreen
import com.breaswl.spexerutil.ui.screens.StatisticsScreen
import com.breaswl.spexerutil.viewmodel.BreathingViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Modes : Screen("modes")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: BreathingViewModel,
    onMusicToggle: (Boolean) -> Unit
) {
    val selectedMode by viewModel.selectedMode.collectAsState()
    val selectedTheme by viewModel.selectedTheme.collectAsState()
    val backgroundMusicEnabled by viewModel.backgroundMusicEnabled.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                selectedMode = selectedMode,
                selectedTheme = selectedTheme,
                onNavigateToModes = {
                    navController.navigate(Screen.Modes.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onSessionComplete = { duration ->
                    viewModel.saveSession(duration)
                }
            )
        }
        
        composable(Screen.Modes.route) {
            ModesScreen(
                selectedMode = selectedMode,
                onModeSelected = { mode ->
                    viewModel.selectMode(mode)
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Statistics.route) {
            StatisticsScreen(
                sessionRepository = viewModel.getSessionRepository(),
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                selectedTheme = selectedTheme,
                backgroundMusicEnabled = backgroundMusicEnabled,
                onThemeChanged = { theme ->
                    viewModel.setTheme(theme)
                },
                onBackgroundMusicChanged = { enabled ->
                    viewModel.setBackgroundMusic(enabled)
                    onMusicToggle(enabled)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToStatistics = {
                    navController.navigate(Screen.Statistics.route)
                }
            )
        }
    }
}


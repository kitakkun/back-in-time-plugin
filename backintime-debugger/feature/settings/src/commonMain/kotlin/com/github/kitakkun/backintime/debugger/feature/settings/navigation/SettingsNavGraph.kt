package com.github.kitakkun.backintime.debugger.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.kitakkun.backintime.debugger.feature.settings.SettingsPage

const val SETTINGS_GRAPH_ROUTE = "settings"

fun NavGraphBuilder.settingsNavGraph(navController: NavController) {
    composable(SETTINGS_GRAPH_ROUTE) {
        SettingsPage()
    }
}

fun NavController.navigateToSettings() {
    navigate(SETTINGS_GRAPH_ROUTE) { launchSingleTop = true }
}

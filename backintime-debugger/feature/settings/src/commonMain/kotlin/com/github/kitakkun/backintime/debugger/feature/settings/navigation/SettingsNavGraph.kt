package com.github.kitakkun.backintime.debugger.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.kitakkun.backintime.debugger.feature.settings.SettingsPage

const val SettingsGraphRoute = "settings"

fun NavGraphBuilder.settingsNavGraph(navController: NavController) {
    composable(SettingsGraphRoute) {
        SettingsPage()
    }
}

fun NavController.navigateToSettings() {
    navigate(SettingsGraphRoute) { launchSingleTop = true }
}

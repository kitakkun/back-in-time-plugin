package com.github.kitakkun.backintime.debugger.feature.log.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.kitakkun.backintime.debugger.feature.log.LogPage

const val LogGraphRoute = "log"

fun NavGraphBuilder.logNavGraph(navController: NavController) {
    composable(LogGraphRoute) {
        LogPage()
    }
}

fun NavController.navigateToLog() {
    navigate(LogGraphRoute) { launchSingleTop = true }
}

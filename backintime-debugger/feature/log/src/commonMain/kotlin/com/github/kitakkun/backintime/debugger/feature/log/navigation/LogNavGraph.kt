package com.github.kitakkun.backintime.debugger.feature.log.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.kitakkun.backintime.debugger.feature.log.LogPage

const val LOG_GRAPH_ROUTE = "log"

fun NavGraphBuilder.logNavGraph(navController: NavController) {
    composable(LOG_GRAPH_ROUTE) {
        LogPage()
    }
}

fun NavController.navigateToLog() {
    navigate(LOG_GRAPH_ROUTE) { launchSingleTop = true }
}

package com.github.kitakkun.backintime.debugger.feature.log.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation

const val LOG_GRAPH_ROUTE = "log"

fun NavGraphBuilder.logNavGraph(navController: NavController) {
    navigation(
        route = LOG_GRAPH_ROUTE,
        startDestination = SESSION_LOG_ROUTE,
    ) {
        sessionLog(navController)
    }
}

fun NavController.navigateToLog() {
    navigate(LOG_GRAPH_ROUTE) { launchSingleTop = true }
}

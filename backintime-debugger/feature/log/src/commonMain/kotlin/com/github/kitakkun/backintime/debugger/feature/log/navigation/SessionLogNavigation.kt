package com.github.kitakkun.backintime.debugger.feature.log.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.kitakkun.backintime.debugger.feature.log.view.session_log.SessionLogPage

const val SESSION_LOG_ROUTE = "session_log"

fun NavGraphBuilder.sessionLog(navController: NavController) {
    composable(route = SESSION_LOG_ROUTE) {
        SessionLogPage(navController)
    }
}

fun NavController.navigateToSessionLog() {
    navigate(SESSION_LOG_ROUTE) { launchSingleTop = true }
}

package com.github.kitakkun.backintime.debugger.feature.connection.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.kitakkun.backintime.debugger.feature.connection.ConnectionPage

const val ConnectionGraphRoute = "connection"

fun NavGraphBuilder.connectionNavGraph(navController: NavController) {
    composable(ConnectionGraphRoute) {
        ConnectionPage()
    }
}

fun NavController.navigateToConnection() {
    navigate(ConnectionGraphRoute) { launchSingleTop = true }
}

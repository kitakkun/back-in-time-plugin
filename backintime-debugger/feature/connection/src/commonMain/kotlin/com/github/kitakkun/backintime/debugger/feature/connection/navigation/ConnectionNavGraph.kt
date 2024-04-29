package com.github.kitakkun.backintime.debugger.feature.connection.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.kitakkun.backintime.debugger.feature.connection.ConnectionPage

const val CONNECTION_GRAPH_ROUTE = "connection"

fun NavGraphBuilder.connectionNavGraph(navController: NavController) {
    composable(CONNECTION_GRAPH_ROUTE) {
        ConnectionPage()
    }
}

fun NavController.navigateToConnection() {
    navigate(CONNECTION_GRAPH_ROUTE) { launchSingleTop = true }
}

package com.github.kitakkun.backintime.debugger.feature.instance.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.kitakkun.backintime.debugger.feature.instance.InstancePage

const val INSTANCE_GRAPH_ROUTE = "instance"

fun NavGraphBuilder.instanceNavGraph(navController: NavController) {
    composable(INSTANCE_GRAPH_ROUTE) {
        InstancePage()
    }
}

fun NavController.navigateToInstance() {
    navigate(INSTANCE_GRAPH_ROUTE) { launchSingleTop = true }
}

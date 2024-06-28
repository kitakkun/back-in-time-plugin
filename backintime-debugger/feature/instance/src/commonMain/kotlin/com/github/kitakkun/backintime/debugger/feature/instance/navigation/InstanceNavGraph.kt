package com.github.kitakkun.backintime.debugger.feature.instance.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation

const val INSTANCE_GRAPH_ROUTE = "instance"

fun NavGraphBuilder.instanceNavGraph(navController: NavController) {
    navigation(
        route = INSTANCE_GRAPH_ROUTE,
        startDestination = INSTANCE_LIST_ROUTE,
    ) {
        instanceList(navController)
        instanceHistory(navController)
    }
}

fun NavController.navigateToInstance() {
    navigate(INSTANCE_GRAPH_ROUTE) { launchSingleTop = true }
}

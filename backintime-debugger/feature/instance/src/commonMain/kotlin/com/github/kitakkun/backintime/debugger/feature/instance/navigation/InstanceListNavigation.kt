package com.github.kitakkun.backintime.debugger.feature.instance.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.InstanceListPage

internal const val INSTANCE_LIST_ROUTE = "list"

internal fun NavGraphBuilder.instanceList(navController: NavController) {
    composable(route = INSTANCE_LIST_ROUTE) {
        InstanceListPage(navController)
    }
}

fun NavController.navigateToInstanceList() {
    navigate(INSTANCE_LIST_ROUTE)
}

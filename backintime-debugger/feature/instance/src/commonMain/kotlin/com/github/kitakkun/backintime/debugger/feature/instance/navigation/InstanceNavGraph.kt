package com.github.kitakkun.backintime.debugger.feature.instance.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.kitakkun.backintime.debugger.feature.instance.InstancePage

const val InstanceGraphRoute = "instance"

fun NavGraphBuilder.instanceNavGraph(navController: NavController) {
    composable(InstanceGraphRoute) {
        InstancePage()
    }
}

fun NavController.navigateToInstance() {
    navigate(InstanceGraphRoute) { launchSingleTop = true }
}

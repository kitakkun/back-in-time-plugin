package com.github.kitakkun.backintime.debugger

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.github.kitakkun.backintime.debugger.feature.connection.navigation.connectionNavGraph
import com.github.kitakkun.backintime.debugger.feature.instance.navigation.InstanceGraphRoute
import com.github.kitakkun.backintime.debugger.feature.instance.navigation.instanceNavGraph
import com.github.kitakkun.backintime.debugger.feature.log.navigation.logNavGraph
import com.github.kitakkun.backintime.debugger.feature.settings.navigation.settingsNavGraph

@Composable
fun BackInTimeDebuggerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = InstanceGraphRoute,
        modifier = modifier,
    ) {
        instanceNavGraph(navController)
        logNavGraph(navController)
        connectionNavGraph(navController)
        settingsNavGraph(navController)
    }
}

package com.github.kitakkun.backintime.debugger

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.kitakkun.backintime.debugger.feature.connection.ConnectionPage
import com.github.kitakkun.backintime.debugger.feature.instance.InstancePage
import com.github.kitakkun.backintime.debugger.feature.log.LogPage
import com.github.kitakkun.backintime.debugger.feature.settings.SettingsPage

@Composable
fun BackInTimeDebuggerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = MainScreen.Instance.route,
        modifier = modifier,
    ) {
        composable(MainScreen.Instance.route) {
            InstancePage()
        }
        composable(MainScreen.Log.route) {
            LogPage()
        }
        composable(MainScreen.Connection.route) {
            ConnectionPage()
        }
        composable(MainScreen.Settings.route) {
            SettingsPage()
        }
    }
}

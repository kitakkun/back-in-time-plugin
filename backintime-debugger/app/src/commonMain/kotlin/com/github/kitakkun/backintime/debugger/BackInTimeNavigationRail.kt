package com.github.kitakkun.backintime.debugger

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BackInTimeDebuggerNavigationRail(
    navController: NavController,
) {
    val currentRoute by navController.currentRouteAsState()

    NavigationRail {
        NavigationRailTabItem(
            screen = MainScreen.Instance,
            currentRoute = currentRoute,
            onClick = {
                navController.navigate(MainScreen.Instance.route) {
                    this.launchSingleTop = true
                    this.restoreState = true
                }
            },
        )
        NavigationRailTabItem(
            screen = MainScreen.Log,
            currentRoute = currentRoute,
            onClick = {
                navController.navigate(MainScreen.Log.route) {
                    this.launchSingleTop = true
                    this.restoreState = true
                }
            },
        )
        Spacer(Modifier.weight(1f))
        NavigationRailTabItem(
            screen = MainScreen.Connection,
            currentRoute = currentRoute,
            onClick = {
                navController.navigate(MainScreen.Connection.route) {
                    this.launchSingleTop = true
                    this.restoreState = true
                }
            },
        )
        NavigationRailTabItem(
            screen = MainScreen.Settings,
            currentRoute = currentRoute,
            onClick = {
                navController.navigate(MainScreen.Settings.route) {
                    this.launchSingleTop = true
                    this.restoreState = true
                }
            },
        )
    }
}

@Composable
private fun NavController.currentRouteAsState(): State<String?> {
    val navBackStackEntry by currentBackStackEntryAsState()
    return produceState(initialValue = navBackStackEntry?.destination?.route) {
        snapshotFlow {
            navBackStackEntry?.destination?.route
        }.collect {
            value = it
        }
    }
}

@Composable
private fun NavigationRailTabItem(screen: MainScreen, currentRoute: String?, onClick: () -> Unit) {
    NavigationRailItem(
        selected = currentRoute?.startsWith(screen.route) == true,
        icon = {
            Icon(
                painter = screen.tabIcon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        },
        label = { Text(screen.tabTitle) },
        onClick = onClick,
    )
}

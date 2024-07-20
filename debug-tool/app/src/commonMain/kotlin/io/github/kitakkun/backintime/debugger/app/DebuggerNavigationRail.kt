package io.github.kitakkun.backintime.debugger.app

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import backintime.debug_tool.app.generated.resources.Res
import backintime.debug_tool.app.generated.resources.connection_tab_title
import backintime.debug_tool.app.generated.resources.ic_instance_tab
import backintime.debug_tool.app.generated.resources.instance_tab_title
import backintime.debug_tool.app.generated.resources.log_tab_title
import backintime.debug_tool.app.generated.resources.settings_tab_title
import io.github.kitakkun.backintime.debugger.feature.connection.ConnectionScreenRoute
import io.github.kitakkun.backintime.debugger.feature.connection.navigateToConnectionScreen
import io.github.kitakkun.backintime.debugger.feature.instance.InstanceTabRoute
import io.github.kitakkun.backintime.debugger.feature.instance.navigateToInstanceTab
import io.github.kitakkun.backintime.debugger.feature.log.LogScreenRoute
import io.github.kitakkun.backintime.debugger.feature.log.navigateToLogScreen
import io.github.kitakkun.backintime.debugger.feature.settings.SettingsScreenRoute
import io.github.kitakkun.backintime.debugger.feature.settings.navigateToSettingsScreen
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DebuggerNavigationRail(
    navController: NavController,
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination by rememberUpdatedState(currentNavBackStackEntry?.destination)

    NavigationRail {
        NavigationRailItem(
            selected = currentDestination?.hasRouteAsParent<InstanceTabRoute>() ?: false,
            iconPainter = painterResource(Res.drawable.ic_instance_tab),
            labelResource = Res.string.instance_tab_title,
            onClick = navController::navigateToInstanceTab,
        )
        NavigationRailItem(
            selected = currentDestination?.hasRouteAsParent<LogScreenRoute>() ?: false,
            iconPainter = rememberVectorPainter(Icons.Default.FilePresent),
            labelResource = Res.string.log_tab_title,
            onClick = navController::navigateToLogScreen,
        )
        Spacer(Modifier.weight(1f))
        NavigationRailItem(
            selected = currentDestination?.hasRouteAsParent<ConnectionScreenRoute>() ?: false,
            iconPainter = rememberVectorPainter(Icons.Default.NetworkCheck),
            labelResource = Res.string.connection_tab_title,
            onClick = navController::navigateToConnectionScreen,
        )
        NavigationRailItem(
            selected = currentDestination?.hasRouteAsParent<SettingsScreenRoute>() ?: false,
            iconPainter = rememberVectorPainter(Icons.Default.Settings),
            labelResource = Res.string.settings_tab_title,
            onClick = navController::navigateToSettingsScreen,
        )
    }
}

@Composable
private inline fun <reified T : Any> NavDestination.hasRouteAsParent(): Boolean {
    if (hasRoute<T>()) return true
    var parentGraph = parent
    while (parentGraph != null) {
        if (parentGraph.hasRoute<T>()) return true
        parentGraph = parentGraph.parent
    }
    return false
}

@Composable
private fun NavigationRailItem(
    selected: Boolean,
    iconPainter: Painter,
    labelResource: StringResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationRailItem(
        selected = selected,
        icon = {
            Icon(
                painter = iconPainter,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        },
        label = { Text(stringResource(labelResource)) },
        onClick = onClick,
        modifier = modifier,
    )
}

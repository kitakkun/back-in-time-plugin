package com.github.kitakkun.backintime.debugger

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
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.github.kitakkun.backintime.app.generated.resources.Res
import com.github.kitakkun.backintime.app.generated.resources.connection_tab_title
import com.github.kitakkun.backintime.app.generated.resources.ic_instance_tab
import com.github.kitakkun.backintime.app.generated.resources.instance_tab_title
import com.github.kitakkun.backintime.app.generated.resources.log_tab_title
import com.github.kitakkun.backintime.app.generated.resources.settings_tab_title
import com.github.kitakkun.backintime.debugger.feature.connection.navigation.CONNECTION_GRAPH_ROUTE
import com.github.kitakkun.backintime.debugger.feature.connection.navigation.navigateToConnection
import com.github.kitakkun.backintime.debugger.feature.instance.navigation.INSTANCE_GRAPH_ROUTE
import com.github.kitakkun.backintime.debugger.feature.instance.navigation.navigateToInstance
import com.github.kitakkun.backintime.debugger.feature.log.navigation.LOG_GRAPH_ROUTE
import com.github.kitakkun.backintime.debugger.feature.log.navigation.navigateToLog
import com.github.kitakkun.backintime.debugger.feature.settings.navigation.SETTINGS_GRAPH_ROUTE
import com.github.kitakkun.backintime.debugger.feature.settings.navigation.navigateToSettings
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BackInTimeDebuggerNavigationRail(
    navController: NavController,
) {
    val currentRoute by navController.currentRouteAsState()

    NavigationRail {
        NavigationRailItem(
            selected = currentRoute == INSTANCE_GRAPH_ROUTE,
            iconPainter = painterResource(Res.drawable.ic_instance_tab),
            labelResource = Res.string.instance_tab_title,
            onClick = { navController.navigateToInstance() },
        )
        NavigationRailItem(
            selected = currentRoute == LOG_GRAPH_ROUTE,
            iconPainter = rememberVectorPainter(Icons.Default.FilePresent),
            labelResource = Res.string.log_tab_title,
            onClick = { navController.navigateToLog() },
        )
        Spacer(Modifier.weight(1f))
        NavigationRailItem(
            selected = currentRoute == CONNECTION_GRAPH_ROUTE,
            iconPainter = rememberVectorPainter(Icons.Default.NetworkCheck),
            labelResource = Res.string.connection_tab_title,
            onClick = { navController.navigateToConnection() },
        )
        NavigationRailItem(
            selected = currentRoute == SETTINGS_GRAPH_ROUTE,
            iconPainter = rememberVectorPainter(Icons.Default.Settings),
            labelResource = Res.string.settings_tab_title,
            onClick = { navController.navigateToSettings() },
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

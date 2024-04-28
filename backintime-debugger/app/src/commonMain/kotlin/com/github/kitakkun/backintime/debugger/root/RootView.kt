package com.github.kitakkun.backintime.debugger.root

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.github.kitakkun.backintime.debugger.feature.connection.ConnectionTab
import com.github.kitakkun.backintime.debugger.feature.instance.InstancesTab
import com.github.kitakkun.backintime.debugger.feature.log.LogTab
import com.github.kitakkun.backintime.debugger.feature.settings.SettingsTab

@Composable
fun RootView(
    snackbarHost: @Composable () -> Unit,
) {
    Scaffold(
        snackbarHost = snackbarHost,
    ) {
        TabNavigator(InstancesTab) {
            Row {
                NavigationRail {
                    TabNavigationRailItem(InstancesTab)
                    TabNavigationRailItem(LogTab)
                    Spacer(modifier = Modifier.weight(1f))
                    TabNavigationRailItem(ConnectionTab)
                    TabNavigationRailItem(SettingsTab)
                }
                CurrentTab()
            }
        }
    }
}

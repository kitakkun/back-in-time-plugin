package com.github.kitakkun.backintime.debugger.root

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.github.kitakkun.backintime.debugger.feature.connection.ConnectionTab
import com.github.kitakkun.backintime.debugger.feature.instance.InstancesTab
import com.github.kitakkun.backintime.debugger.feature.log.LogTab
import com.github.kitakkun.backintime.debugger.feature.settings.SettingsTab

@Composable
fun RootView(
    state: RootState,
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
                if (!state.isServerRunning) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text("Starting WebSocket Server...")
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator()
                    }
                } else {
                    CurrentTab()
                }
            }
        }
    }
}

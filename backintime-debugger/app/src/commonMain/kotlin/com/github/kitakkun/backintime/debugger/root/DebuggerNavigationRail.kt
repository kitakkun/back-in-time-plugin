package com.github.kitakkun.backintime.debugger.root

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab

@Composable
fun TabNavigationRailItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    NavigationRailItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = { tab.options.icon?.let { Icon(painter = it, contentDescription = null, modifier = Modifier.size(24.dp)) } },
        label = { Text(text = tab.options.title) },
    )
}

package com.github.kitakkun.backintime.debugger.feature.instance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object InstancesTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val title = "Instances"
            val icon = painterResource("instance-fill.svg")

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon,
                )
            }
        }

    @Composable
    override fun Content() {
        // TODO
    }
}

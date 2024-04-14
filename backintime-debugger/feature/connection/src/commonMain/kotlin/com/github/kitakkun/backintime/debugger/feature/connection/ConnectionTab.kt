package com.github.kitakkun.backintime.debugger.feature.connection

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object ConnectionTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val icon = rememberVectorPainter(image = Icons.Default.NetworkCheck)
            return remember {
                TabOptions(
                    index = 3u,
                    title = "Connection",
                    icon = icon,
                )
            }
        }

    @Composable
    override fun Content() {
        // TODO
    }
}

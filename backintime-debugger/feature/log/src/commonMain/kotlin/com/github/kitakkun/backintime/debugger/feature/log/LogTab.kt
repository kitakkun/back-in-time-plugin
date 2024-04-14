package com.github.kitakkun.backintime.debugger.feature.log

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object LogTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val icon = rememberVectorPainter(Icons.Default.FilePresent)
            return remember {
                TabOptions(
                    index = 2u,
                    title = "Log",
                    icon = icon,
                )
            }
        }

    @Composable
    override fun Content() {
        // TODO
    }
}

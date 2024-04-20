package com.github.kitakkun.backintime.debugger.feature.connection

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.github.kitakkun.backintime.connection.generated.resources.Res
import com.github.kitakkun.backintime.connection.generated.resources.connection_tab_title
import org.jetbrains.compose.resources.stringResource

object ConnectionTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val icon = rememberVectorPainter(image = Icons.Default.NetworkCheck)
            val title = stringResource(Res.string.connection_tab_title)
            return remember {
                TabOptions(
                    index = 3u,
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

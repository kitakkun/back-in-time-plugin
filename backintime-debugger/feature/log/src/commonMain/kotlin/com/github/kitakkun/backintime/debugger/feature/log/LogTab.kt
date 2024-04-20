package com.github.kitakkun.backintime.debugger.feature.log

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.github.kitakkun.backintime.log.generated.resources.Res
import com.github.kitakkun.backintime.log.generated.resources.log_tab_title
import org.jetbrains.compose.resources.stringResource

object LogTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val title = stringResource(Res.string.log_tab_title)
            val icon = rememberVectorPainter(Icons.Default.FilePresent)
            return remember {
                TabOptions(
                    index = 2u,
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

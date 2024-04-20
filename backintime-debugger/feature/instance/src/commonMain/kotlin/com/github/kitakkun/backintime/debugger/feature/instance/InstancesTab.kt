package com.github.kitakkun.backintime.debugger.feature.instance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.github.kitakkun.backintime.instance.generated.resources.Res
import com.github.kitakkun.backintime.instance.generated.resources.ic_instance_tab
import com.github.kitakkun.backintime.instance.generated.resources.instance_tab_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

object InstancesTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val title = stringResource(Res.string.instance_tab_title)
            val icon = painterResource(Res.drawable.ic_instance_tab)

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

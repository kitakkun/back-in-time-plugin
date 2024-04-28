package com.github.kitakkun.backintime.debugger.feature.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.github.kitakkun.backintime.settings.generated.resources.Res
import com.github.kitakkun.backintime.settings.generated.resources.settings_tab_title
import org.jetbrains.compose.resources.stringResource

object SettingsTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val title = stringResource(Res.string.settings_tab_title)
            val icon = rememberVectorPainter(image = Icons.Default.Settings)
            return remember {
                TabOptions(
                    index = 4u,
                    title = title,
                    icon = icon,
                )
            }
        }

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SettingsTabScreenModel>()
        val bindModel by screenModel.bindModel.collectAsState()

        SettingsTabView(
            bindModel = bindModel,
            onChangeWebSocketPort = screenModel::updateWebSocketPort,
            onChangeDeleteSessionDataOnDisconnect = screenModel::updateDeleteSessionDataOnDisconnect,
        )
    }
}

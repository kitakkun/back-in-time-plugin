package com.github.kitakkun.backintime.debugger

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.github.kitakkun.backintime.app.generated.resources.Res
import com.github.kitakkun.backintime.app.generated.resources.connection_tab_title
import com.github.kitakkun.backintime.app.generated.resources.ic_instance_tab
import com.github.kitakkun.backintime.app.generated.resources.instance_tab_title
import com.github.kitakkun.backintime.app.generated.resources.log_tab_title
import com.github.kitakkun.backintime.app.generated.resources.settings_tab_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

sealed interface MainScreen {
    val route: String
    val tabTitle: String @Composable get
    val tabIcon: Painter @Composable get

    data object Instance : MainScreen {
        override val route: String = "instance"
        override val tabTitle: String @Composable get() = stringResource(Res.string.instance_tab_title)
        override val tabIcon: Painter @Composable get() = painterResource(Res.drawable.ic_instance_tab)
    }

    data object Log : MainScreen {
        override val route: String = "log"
        override val tabTitle: String @Composable get() = stringResource(Res.string.log_tab_title)
        override val tabIcon: Painter @Composable get() = rememberVectorPainter(Icons.Default.FilePresent)
    }

    data object Connection : MainScreen {
        override val route: String = "connection"
        override val tabTitle: String @Composable get() = stringResource(Res.string.connection_tab_title)
        override val tabIcon: Painter @Composable get() = rememberVectorPainter(image = Icons.Default.NetworkCheck)
    }

    data object Settings : MainScreen {
        override val route: String = "settings"
        override val tabTitle: String @Composable get() = stringResource(Res.string.settings_tab_title)
        override val tabIcon: Painter @Composable get() = rememberVectorPainter(Icons.Default.Settings)
    }
}
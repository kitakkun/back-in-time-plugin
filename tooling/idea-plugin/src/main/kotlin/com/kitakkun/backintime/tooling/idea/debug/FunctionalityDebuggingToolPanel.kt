package com.kitakkun.backintime.tooling.idea.debug

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.unit.dp
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.kitakkun.backintime.tooling.idea.service.IDENavigatorImpl
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme

/**
 * Just for debugging purpose.
 * Swap [com.kitakkun.backintime.tooling.idea.BackInTimeToolComposePanel] in the [com.kitakkun.backintime.tooling.idea.BackInTimeToolWindowFactory] with this to test.
 */
class FunctionalityDebuggingToolPanel(project: Project) {
    private val ideNavigator = project.service<IDENavigatorImpl>()

    val panel = ComposePanel().apply {
        setContent {
            IntUiTheme(isDark = isSystemInDarkTheme()) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    IDENavigatorDebugSection(ideNavigator)
                }
            }
        }
    }
}

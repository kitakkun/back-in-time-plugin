package com.kitakkun.backintime.tooling.idea

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class BackInTimeToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val content = ContentFactory.getInstance().createContent(
            BackInTimeToolComposePanel(project).panel, null, false
        )
        toolWindow.contentManager.addContent(content)
    }
}

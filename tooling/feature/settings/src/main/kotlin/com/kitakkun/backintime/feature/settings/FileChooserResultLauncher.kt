package com.kitakkun.backintime.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.io.File
import javax.swing.JFileChooser

@Composable
fun rememberFileChooserResultLauncher(
    resultHandler: (File?) -> Unit,
): FileChooserResultLauncher {
    return remember {
        object : FileChooserResultLauncher {
            override fun launch(chooserConfiguration: JFileChooser.() -> Unit) {
                val fileChooser = JFileChooser().apply {
                    chooserConfiguration()
                }
                val result = fileChooser.showSaveDialog(null)
                if (result == JFileChooser.APPROVE_OPTION) {
                    resultHandler(fileChooser.selectedFile.absoluteFile)
                } else {
                    resultHandler(null)
                }
            }
        }
    }
}

interface FileChooserResultLauncher {
    fun launch(chooserConfiguration: JFileChooser.() -> Unit)
}

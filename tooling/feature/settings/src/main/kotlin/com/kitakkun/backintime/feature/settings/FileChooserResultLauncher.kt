package com.kitakkun.backintime.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.io.File
import javax.swing.JFileChooser

@Composable
fun rememberFileChooserResultLauncher(
    onPicked: (File) -> Unit,
    onCanceled: () -> Unit,
): FileChooserResultLauncher {
    return remember {
        object : FileChooserResultLauncher {
            override fun launch(chooserConfiguration: JFileChooser.() -> Unit) {
                val fileChooser = JFileChooser().apply {
                    chooserConfiguration()
                }
                val result = fileChooser.showSaveDialog(null)
                if (result == JFileChooser.APPROVE_OPTION) {
                    onPicked(fileChooser.selectedFile.absoluteFile)
                } else {
                    onCanceled()
                }
            }
        }
    }
}

interface FileChooserResultLauncher {
    fun launch(chooserConfiguration: JFileChooser.() -> Unit)
}

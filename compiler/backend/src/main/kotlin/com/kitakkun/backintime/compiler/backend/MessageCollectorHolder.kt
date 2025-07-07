@file:Suppress("UNUSED")

package com.kitakkun.backintime.compiler.backend

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

object MessageCollectorHolder {
    lateinit var messageCollector: MessageCollector

    fun reportWarning(message: String) {
        messageCollector.report(CompilerMessageSeverity.WARNING, message)
    }

    fun reportError(message: String) {
        messageCollector.report(CompilerMessageSeverity.ERROR, message)
    }
}

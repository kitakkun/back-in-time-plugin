package com.github.kitakkun.backintime.compiler

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

object MessageCollectorHolder {
    lateinit var messageCollector: MessageCollector

    @Suppress("UNUSED")
    fun reportWarning(message: String) {
        messageCollector.report(CompilerMessageSeverity.WARNING, message)
    }

    @Suppress("UNUSED")
    fun reportError(message: String) {
        messageCollector.report(CompilerMessageSeverity.ERROR, message)
    }
}

package io.github.kitakkun.backintime.debugger.core.model

enum class Language(val preferencesKey: String) {
    ENGLISH("en"),
    JAPANESE("ja"),
    SYSTEM("system");

    companion object {
        fun convert(preferencesKey: String): Language {
            return entries.first { it.preferencesKey == preferencesKey }
        }
    }
}

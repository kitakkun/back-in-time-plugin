package com.kitakkun.backintime.compiler.yaml

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import org.intellij.lang.annotations.Language

class BackInTimeYamlConfigurationParser {
    fun parse(@Language("yaml") string: String): BackInTimeYamlConfiguration {
        return Yaml.default.decodeFromString(string)
    }
}

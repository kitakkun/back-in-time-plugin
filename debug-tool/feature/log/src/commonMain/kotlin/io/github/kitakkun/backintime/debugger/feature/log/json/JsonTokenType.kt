package io.github.kitakkun.backintime.debugger.feature.log.json

enum class JsonTokenType {
    OBJECT_START,
    OBJECT_END,
    ARRAY_START,
    ARRAY_END,
    KEY,
    VALUE,
    COMMA,
}

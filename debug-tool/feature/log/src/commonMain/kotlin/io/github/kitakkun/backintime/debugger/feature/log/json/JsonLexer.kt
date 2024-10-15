package io.github.kitakkun.backintime.debugger.feature.log.json

class JsonLexer(
    private val source: String,
) {
    private var current: Int = 0

    fun advance() {
        val char = source[current++]
    }
}

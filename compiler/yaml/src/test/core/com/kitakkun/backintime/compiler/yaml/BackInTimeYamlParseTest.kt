package com.kitakkun.backintime.compiler.yaml

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.intellij.lang.annotations.Language
import kotlin.test.Test
import kotlin.test.assertEquals

class BackInTimeYamlParseTest {
    @Test
    fun testEmpty() {
        val result = BackInTimeYamlConfigurationParser().parse(
            """
            trackableStateHolders: []
            """.trimIndent()
        )
        assertEquals(
            expected = BackInTimeYamlConfiguration(trackableStateHolders = emptyList()),
            actual = result,
        )
    }

    @Test
    fun testWithTraceableStateHolders() {
        @Language("yaml")
        val source = """
            trackableStateHolders:
              - classId: "kotlinx/coroutines/flow/MutableStateFlow"
                accessor:
                  getter: "<get-value>"
                  setter: "<set-value>"
                  preSetter: null
                captures:
                  - signature: "<set-value>"
                    strategy: "arg0"
                  - signature: "kotlinx/coroutines/flow/update"
                    strategy: "afterCall"
                  - signature: "myextension/Receiver myextension/coroutines/test(*, 0, kotlin/Int)"
                    strategy: "arg0"
                serializeAs: "0"
        """.trimIndent()
        val result = BackInTimeYamlConfigurationParser().parse(source)
        assertEquals(
            expected = BackInTimeYamlConfiguration(
                trackableStateHolders = listOf(
                    TrackableStateHolder(
                        classId = "kotlinx/coroutines/flow/MutableStateFlow",
                        accessor = StateAccessor(
                            getter = CallableSignature.PropertyAccessor.Getter("value"),
                            setter = CallableSignature.PropertyAccessor.Setter("value"),
                        ),
                        serializeAs = TypeSignature.Generic(0),
                        captures = listOf(
                            CaptureTarget(
                                signature = CallableSignature.PropertyAccessor.Setter("value"),
                                strategy = CaptureStrategy.ValueArgument(0),
                            ),
                            CaptureTarget(
                                signature = CallableSignature.NamedFunction.TopLevel(
                                    "",
                                    "kotlinx.coroutines.flow",
                                    "update",
                                    valueParameters = ParametersSignature.Any,
                                ),
                                strategy = CaptureStrategy.AfterCall,
                            ),
                            CaptureTarget(
                                signature = CallableSignature.NamedFunction.TopLevel(
                                    "myextension/Receiver",
                                    "myextension.coroutines",
                                    "test",
                                    valueParameters = ParametersSignature.Specified(
                                        parameterTypes = listOf(
                                            TypeSignature.Any,
                                            TypeSignature.Generic(0),
                                            TypeSignature.Class("kotlin/Int")
                                        )
                                    ),
                                ),
                                strategy = CaptureStrategy.ValueArgument(0),
                            )
                        )
                    )
                ),
            ),
            actual = result,
        )
        assertEquals(
            expected = result,
            actual = with(Yaml.default) { decodeFromString(encodeToString(result)) },
        )
    }

    @Test
    fun builtInConfigFileParseTest() {
        val text = javaClass.classLoader.getResource("backintime.yaml")?.readText()!!
        println(BackInTimeYamlConfigurationParser().parse(text))
    }
}
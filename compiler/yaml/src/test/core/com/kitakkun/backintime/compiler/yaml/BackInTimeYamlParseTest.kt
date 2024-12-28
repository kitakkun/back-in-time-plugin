package com.kitakkun.backintime.compiler.yaml

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
        val result = BackInTimeYamlConfigurationParser().parse(
            """
            trackableStateHolders:
              - classId: "kotlinx/coroutines/flow/MutableStateFlow"
                serializeAs: "0"
                accessor:
                  getter: "<get-value>"
                  setter: "<set-value>"
                captures:
                  - signature: "<set-value>"
                    strategy: "arg0"
                  - signature: "kotlinx/coroutines/flow/update"
                    strategy: "afterCall"
            """.trimIndent()
        )
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
                                signature = CallableSignature.NamedFunction.TopLevel("kotlinx.coroutines.flow", "update"),
                                strategy = CaptureStrategy.AfterCall,
                            )
                        )
                    )
                ),
            ),
            actual = result,
        )
    }

    @Test
    fun builtInConfigFileParseTest() {
        val text = javaClass.classLoader.getResource("backintime.yaml")?.readText()!!
        BackInTimeYamlConfigurationParser().parse(text)
    }
}
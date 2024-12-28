package com.kitakkun.backintime.compiler.yaml

import kotlin.test.Test
import kotlin.test.assertEquals

class BackInTimeYamlParseTest {
    @Test
    fun testEmpty() {
        val result = BackInTimeYamlConfigurationParser().parse(
            """
            enabled: true
            trackableStateHolders: []
            """.trimIndent()
        )
        assertEquals(
            expected = BackInTimeYamlConfiguration(
                enabled = true,
                trackableStateHolders = emptyList(),
            ),
            actual = result,
        )
    }

    @Test
    fun testWithTraceableStateHolders() {
        val result = BackInTimeYamlConfigurationParser().parse(
            """
            enabled: true
            trackableStateHolders:
              - classId: kotlinx/coroutines/flow/MutableStateFlow
                accessor:
                  getter: <get-value>
                  setter: <set-value>
                captures:
                  - signature: <set-value>
                    strategy: arg0
            """.trimIndent()
        )
        assertEquals(
            expected = BackInTimeYamlConfiguration(
                enabled = true,
                trackableStateHolders = listOf(
                    TrackableStateHolder(
                        classId = "kotlinx/coroutines/flow/MutableStateFlow",
                        accessor = StateAccessor(
                            getter = CallableSignature.PropertyAccessor.Getter("value"),
                            setter = CallableSignature.PropertyAccessor.Setter("value"),
                        ),
                        captures = listOf(
                            CaptureTarget(
                                signature = CallableSignature.PropertyAccessor.Setter("value"),
                                strategy = CaptureStrategy.ValueArgument(0),
                            )
                        )
                    )
                ),
            ),
            actual = result,
        )
    }
}
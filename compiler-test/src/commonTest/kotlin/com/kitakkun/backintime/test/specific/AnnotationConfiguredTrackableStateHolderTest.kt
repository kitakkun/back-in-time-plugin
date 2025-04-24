package com.kitakkun.backintime.test.specific

import com.kitakkun.backintime.core.annotations.BackInTime
import com.kitakkun.backintime.core.annotations.Capture
import com.kitakkun.backintime.core.annotations.Getter
import com.kitakkun.backintime.core.annotations.Setter
import com.kitakkun.backintime.core.runtime.BackInTimeDebuggable
import com.kitakkun.backintime.test.base.BackInTimeDebugServiceTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AnnotationConfiguredTrackableStateHolderTest : BackInTimeDebugServiceTest() {
    companion object {
        private const val TARGET_PROPERTY_SIGNATURE = "com/kitakkun/backintime/test/specific/AnnotationConfiguredTrackableStateHolderTest.TrackableStateHolderOwner.trackableStateHolder"
    }

    @com.kitakkun.backintime.core.annotations.TrackableStateHolder
    private class AnnotationConfiguredTrackableStateHolder<T>(
        @Getter @Setter @Capture var value: T,
    ) {
        @Capture
        fun update(newValue: T) {
            value = newValue
        }
    }

    @BackInTime
    private class TrackableStateHolderOwner {
        val trackableStateHolder = AnnotationConfiguredTrackableStateHolder(0)

        fun updateContainerValue(value: Int) {
            trackableStateHolder.value = value
        }
    }

    @Test
    fun captureTest() = runBlocking {
        val owner = TrackableStateHolderOwner()
        assertIs<BackInTimeDebuggable>(owner)

        owner.updateContainerValue(10)
        delay(100)

        assertEquals(10, owner.trackableStateHolder.value)
        assertEquals(1, notifyValueChangeEvents.size)
        assertEquals(owner.backInTimeInstanceUUID, notifyValueChangeEvents[0].instanceUUID)
        assertEquals(TARGET_PROPERTY_SIGNATURE, notifyValueChangeEvents[0].propertySignature)
        assertEquals(10, notifyValueChangeEvents[0].value.toInt())
    }

    @Test
    fun forceSetTest() {
        val owner = TrackableStateHolderOwner()
        assertIs<BackInTimeDebuggable>(owner)

        owner.forceSetValue(propertySignature = TARGET_PROPERTY_SIGNATURE, jsonValue = "10")
        assertEquals(10, owner.trackableStateHolder.value)
    }
}

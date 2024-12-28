package com.kitakkun.backintime.compiler.backend.valuecontainer.raw

import com.kitakkun.backintime.compiler.backend.valuecontainer.match.FunctionPredicate
import org.jetbrains.kotlin.name.ClassId

class RawValueContainerBuilder(
    val classId: ClassId,
    val isSelfContained: Boolean,
) {
    lateinit var setter: List<FunctionPredicate>
    lateinit var captureTargets: List<Pair<FunctionPredicate, CaptureStrategy>>

    // null for self-contained
    var getter: FunctionPredicate? = null
    var serializeAs: ClassId? = null

    fun build(): RawValueContainer {
        return when {
            isSelfContained -> RawValueContainer.SelfContained(
                classId = classId,
                setter = setter,
                captureTargets = captureTargets,
                serializeAs = serializeAs,
            )

            else -> RawValueContainer.Wrapper(
                classId = classId,
                getter = getter!!,
                setter = setter,
                captureTargets = captureTargets,
                serializeAs = serializeAs,
            )
        }
    }
}

fun selfContainedValueContainer(classId: ClassId, init: RawValueContainerBuilder.() -> Unit = {}): RawValueContainer.SelfContained {
    return RawValueContainerBuilder(
        classId = classId,
        isSelfContained = true,
    ).apply(init).build() as RawValueContainer.SelfContained
}

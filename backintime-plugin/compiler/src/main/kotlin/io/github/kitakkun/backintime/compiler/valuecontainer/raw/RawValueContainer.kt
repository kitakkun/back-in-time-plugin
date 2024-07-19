package io.github.kitakkun.backintime.compiler.valuecontainer.raw

import io.github.kitakkun.backintime.compiler.valuecontainer.match.FunctionPredicate
import org.jetbrains.kotlin.name.ClassId

sealed class RawValueContainer {
    abstract val classId: ClassId
    abstract val setter: List<FunctionPredicate>
    abstract val captureTargets: List<Pair<FunctionPredicate, CaptureStrategy>>
    abstract val serializeAs: ClassId?

    data class Wrapper(
        override val classId: ClassId,
        val getter: FunctionPredicate,
        override val setter: List<FunctionPredicate>,
        override val captureTargets: List<Pair<FunctionPredicate, CaptureStrategy>>,
        override val serializeAs: ClassId?,
    ) : RawValueContainer()

    data class SelfContained(
        override val classId: ClassId,
        override val setter: List<FunctionPredicate>,
        override val captureTargets: List<Pair<FunctionPredicate, CaptureStrategy>>,
        override val serializeAs: ClassId?,
    ) : RawValueContainer()
}

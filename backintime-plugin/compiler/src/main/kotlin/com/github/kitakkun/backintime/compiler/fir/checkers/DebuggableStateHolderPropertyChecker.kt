package com.github.kitakkun.backintime.compiler.fir.checkers

import com.github.kitakkun.backintime.compiler.consts.BackInTimeAnnotations
import com.github.kitakkun.backintime.compiler.fir.extension.backInTimeCompilerConfigurationProvider
import com.github.kitakkun.backintime.compiler.fir.matcher.debuggableStateHolderPredicateMatcher
import com.github.kitakkun.backintime.compiler.fir.matcher.valueContainerPredicateMatcher
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirRegularClassChecker
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.toRegularClassSymbol
import org.jetbrains.kotlin.fir.types.type
import org.jetbrains.kotlin.javac.resolve.classId

object DebuggableStateHolderPropertyChecker : FirRegularClassChecker(MppCheckerKind.Platform) {
    private val serializableAnnotationClassId = classId("kotlinx.serialization", "Serializable")

    override fun check(declaration: FirRegularClass, context: CheckerContext, reporter: DiagnosticReporter) {
        with(context) {
            if (declaration.hasAnnotation(BackInTimeAnnotations.backInTimeAnnotationClassId, session)) {
                val memberProperties = declaration.declarations.filterIsInstance<FirProperty>()
                memberProperties.forEach { property -> checkProperty(property, reporter, context) }
            }
        }
    }

    context(CheckerContext)
    private fun checkProperty(
        declaration: FirProperty,
        reporter: DiagnosticReporter,
        context: CheckerContext,
    ) {
        val propertyType = declaration.returnTypeRef.coneType
        if (propertyType.isBuiltinSerializable()) return
        if (propertyType.hasSerializableAnnotation()) return
        if (propertyType.isDebuggableStateHolder()) return

        if (!propertyType.isValueContainer()) {
            return reporter.reportOn(declaration.source, FirBackInTimeErrors.PROPERTY_VALUE_MUST_BE_SERIALIZABLE, context)
        }

        val typeArguments = propertyType.typeArguments
        if (typeArguments.size > 2) {
            return reporter.reportOn(declaration.source, FirBackInTimeErrors.VALUE_CONTAINER_MORE_THAN_TWO_TYPE_ARGUMENTS, context)
        } else if (typeArguments.size == 1) {
            val valueType = typeArguments.single().type ?: return
            if (valueType.isBuiltinSerializable()) return
            if (valueType.hasSerializableAnnotation()) return
            return reporter.reportOn(declaration.source, FirBackInTimeErrors.PROPERTY_VALUE_MUST_BE_SERIALIZABLE, context)
        }
    }

    /**
     * FYI: JetBrains/kotlin/plugins/kotlinx-serialization/kotlinx-serialization.common/src/org/jetbrains/kotlinx/serialization/compiler/resolve/NamingConventions.kt
     */
    private fun ConeKotlinType.isBuiltinSerializable(): Boolean {
        return when (classId?.asSingleFqName()?.asString()) {
            null -> false
            "kotlin.Unit",
            "kotlin.Nothing",
            "kotlin.Boolean",
            "kotlin.Byte",
            "kotlin.Short",
            "kotlin.Int",
            "kotlin.Long",
            "kotlin.Float",
            "kotlin.Double",
            "kotlin.Char",
            "kotlin.UInt",
            "kotlin.ULong",
            "kotlin.UByte",
            "kotlin.UShort",
            "kotlin.String",
            "kotlin.Pair",
            "kotlin.Triple",
            "kotlin.collections.Collection",
            "kotlin.collections.List",
            "kotlin.collections.ArrayList",
            "kotlin.collections.MutableList",
            "kotlin.collections.Set",
            "kotlin.collections.LinkedHashSet",
            "kotlin.collections.MutableSet",
            "kotlin.collections.HashSet",
            "kotlin.collections.Map",
            "kotlin.collections.LinkedHashMap",
            "kotlin.collections.MutableMap",
            "kotlin.collections.HashMap",
            "kotlin.collections.Map.Entry",
            "kotlin.ByteArray",
            "kotlin.ShortArray",
            "kotlin.IntArray",
            "kotlin.LongArray",
            "kotlin.UByteArray",
            "kotlin.UShortArray",
            "kotlin.UIntArray",
            "kotlin.ULongArray",
            "kotlin.CharArray",
            "kotlin.FloatArray",
            "kotlin.DoubleArray",
            "kotlin.BooleanArray",
            "kotlin.time.Duration",
            "java.lang.Boolean",
            "java.lang.Byte",
            "java.lang.Short",
            "java.lang.Integer",
            "java.lang.Long",
            "java.lang.Float",
            "java.lang.Double",
            "java.lang.Character",
            "java.lang.String",
            "java.util.Collection",
            "java.util.List",
            "java.util.ArrayList",
            "java.util.Set",
            "java.util.LinkedHashSet",
            "java.util.HashSet",
            "java.util.Map",
            "java.util.LinkedHashMap",
            "java.util.HashMap",
            "java.util.Map.Entry",
            -> true

            else -> false
        }
    }

    context(CheckerContext)
    private fun ConeKotlinType.hasSerializableAnnotation(): Boolean {
        return this.toRegularClassSymbol(session)?.hasAnnotation(serializableAnnotationClassId, session) == true
    }

    context(CheckerContext)
    private fun ConeKotlinType.isValueContainer(): Boolean {
        val predefinedInGradle = session.backInTimeCompilerConfigurationProvider.config.valueContainers.any { it.classId == this.classId }
        val configuredViaAnnotation = this.toRegularClassSymbol(session)?.let { session.valueContainerPredicateMatcher.isAnnotated(it) } ?: false
        return predefinedInGradle || configuredViaAnnotation
    }

    context(CheckerContext)
    private fun ConeKotlinType.isDebuggableStateHolder(): Boolean {
        return this.toRegularClassSymbol(session)?.let { session.debuggableStateHolderPredicateMatcher.isAnnotated(it) } ?: false
    }
}

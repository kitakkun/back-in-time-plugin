package com.github.kitakkun.backintime.fir.checkers

import com.github.kitakkun.backintime.BackInTimeAnnotations
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirPropertyChecker
import org.jetbrains.kotlin.fir.analysis.checkers.findClosestClassOrObject
import org.jetbrains.kotlin.fir.analysis.checkers.toRegularClassSymbol
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.resolve.providers.dependenciesSymbolProvider
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.toFirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.type
import org.jetbrains.kotlin.javac.resolve.classId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object BackInTimeFirPropertyChecker : FirPropertyChecker() {
    private val serializableAnnotationClassId = classId("kotlinx.serialization", "Serializable")

    override fun check(declaration: FirProperty, context: CheckerContext, reporter: DiagnosticReporter) {
        with(context) {
            val parentClass = context.findClosestClassOrObject() ?: return
            if (!parentClass.hasAnnotation(BackInTimeAnnotations.debuggableStateHolderAnnotationClassId, session)) return

            val valueType = declaration.getValueType()
            if (!valueType.isSerializable()) {
                with(context) {
                    reporter.reportOn(declaration.source, FirBackInTimeErrors.PROPERTY_VALUE_MUST_BE_SERIALIZABLE)
                }
            }
        }
    }

    context(CheckerContext)
    private fun FirTypeRef.isSerializable(): Boolean {
        val builtinSerializers = session.dependenciesSymbolProvider.getTopLevelFunctionSymbols(FqName("kotlinx.serialization.builtins"), Name.identifier("serializer"))
        if (builtinSerializers.any { it.resolvedReturnTypeRef.coneType.typeArguments.firstOrNull()?.type == this.coneType }) return true
        val classSymbol = this.toRegularClassSymbol(session) ?: return false
        return classSymbol.hasAnnotation(serializableAnnotationClassId, session)
    }

    context(CheckerContext)
    private fun FirProperty.getValueType(): FirTypeRef {
        return this.returnTypeRef.coneType.typeArguments.firstOrNull()?.type?.toFirResolvedTypeRef() ?: this.returnTypeRef
    }
}

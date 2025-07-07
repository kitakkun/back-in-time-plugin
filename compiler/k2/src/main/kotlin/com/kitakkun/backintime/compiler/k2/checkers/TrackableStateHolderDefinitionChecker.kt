package com.kitakkun.backintime.compiler.k2.checkers

import com.kitakkun.backintime.compiler.common.BackInTimeAnnotations
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirRegularClassChecker
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.hasAnnotationSafe

object TrackableStateHolderDefinitionChecker : FirRegularClassChecker(MppCheckerKind.Platform) {
    override fun check(declaration: FirRegularClass, context: CheckerContext, reporter: DiagnosticReporter) {
        with(context) {
            if (!declaration.hasAnnotationSafe(BackInTimeAnnotations.trackableStateHolderAnnotationClassId, session)) return
            val functions = declaration.declarations.filterIsInstance<FirSimpleFunction>()
            val properties = declaration.declarations.filterIsInstance<FirProperty>()
            val functionsAndProperties = functions + properties

            val getterAnnotatedDeclarations = functionsAndProperties.filter { it.hasAnnotationSafe(BackInTimeAnnotations.getterAnnotationClassId, session) }
            val setterAnnotatedDeclarations = functionsAndProperties.filter { it.hasAnnotationSafe(BackInTimeAnnotations.setterAnnotationClassId, session) }
            val captureCallExists = functionsAndProperties.any { it.hasAnnotationSafe(BackInTimeAnnotations.captureAnnotationClassId, session) }

            if (getterAnnotatedDeclarations.isEmpty()) {
                reporter.reportOn(declaration.source, FirBackInTimeErrors.MISSING_PROPERTY_GETTER, context)
            } else if (getterAnnotatedDeclarations.size > 1) {
                reporter.reportOn(declaration.source, FirBackInTimeErrors.MULTIPLE_PROPERTY_GETTER, context)
            }

            if (setterAnnotatedDeclarations.isEmpty()) {
                reporter.reportOn(declaration.source, FirBackInTimeErrors.MISSING_PROPERTY_SETTER, context)
            } else if (setterAnnotatedDeclarations.size > 1) {
                reporter.reportOn(declaration.source, FirBackInTimeErrors.MULTIPLE_PROPERTY_SETTER, context)
            }

            if (!captureCallExists) {
                reporter.reportOn(declaration.source, FirBackInTimeErrors.MISSING_CAPTURE_CALL, context)
            }
        }
    }
}

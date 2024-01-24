package com.github.kitakkun.backintime.compiler.fir.checkers

import com.github.kitakkun.backintime.compiler.BackInTimeAnnotations
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirRegularClassChecker
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.hasAnnotationSafe

object ValueContainerDefinitionChecker : FirRegularClassChecker() {
    override fun check(declaration: FirRegularClass, context: CheckerContext, reporter: DiagnosticReporter) {
        with(context) {
            if (!declaration.hasAnnotationSafe(BackInTimeAnnotations.valueContainerAnnotationClassId, session)) return
            val functions = declaration.declarations.filterIsInstance<FirSimpleFunction>()
            val properties = declaration.declarations.filterIsInstance<FirProperty>()
            val functionsAndProperties = functions + properties

            val getterAnnotatedDeclarations = functionsAndProperties.filter { it.hasAnnotationSafe(BackInTimeAnnotations.getterAnnotationClassId, session) }
            val setterAnnotatedDeclarations = functionsAndProperties.filter { it.hasAnnotationSafe(BackInTimeAnnotations.setterAnnotationClassId, session) }
            val captureCallExists = functionsAndProperties.any { it.hasAnnotationSafe(BackInTimeAnnotations.captureAnnotationClassId, session) }

            if (getterAnnotatedDeclarations.isEmpty()) {
                reporter.reportOn(declaration.source, FirBackInTimeErrors.MISSING_PROPERTY_GETTER)
            } else if (getterAnnotatedDeclarations.size > 1) {
                reporter.reportOn(declaration.source, FirBackInTimeErrors.MULTIPLE_PROPERTY_GETTER)
            }

            if (setterAnnotatedDeclarations.isEmpty()) {
                reporter.reportOn(declaration.source, FirBackInTimeErrors.MISSING_PROPERTY_SETTER)
            } else if (setterAnnotatedDeclarations.size > 1) {
                reporter.reportOn(declaration.source, FirBackInTimeErrors.MULTIPLE_PROPERTY_SETTER)
            }

            if (!captureCallExists) {
                reporter.reportOn(declaration.source, FirBackInTimeErrors.MISSING_CAPTURE_CALL)
            }
        }
    }
}

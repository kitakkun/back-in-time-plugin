package com.kitakkun.backintime.compiler.k2.checkers

import com.kitakkun.backintime.compiler.common.BackInTimeAnnotations
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirRegularClassChecker
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.declarations.hasAnnotationSafe
import org.jetbrains.kotlin.fir.declarations.processAllDeclarations
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol

object TrackableStateHolderDefinitionChecker : FirRegularClassChecker(MppCheckerKind.Platform) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirRegularClass) {
        with(context) {
            if (!declaration.hasAnnotationSafe(BackInTimeAnnotations.trackableStateHolderAnnotationClassId, session)) return

            val functionsAndProperties = mutableListOf<FirCallableSymbol<*>>()

            declaration.processAllDeclarations(session) {
                when (it) {
                    is FirCallableSymbol<*> -> functionsAndProperties.add(it)
                    else -> return@processAllDeclarations
                }
            }

            val getterAnnotatedDeclarations = functionsAndProperties.filter { it.hasAnnotation(BackInTimeAnnotations.getterAnnotationClassId, session) }
            val setterAnnotatedDeclarations = functionsAndProperties.filter { it.hasAnnotation(BackInTimeAnnotations.setterAnnotationClassId, session) }
            val captureCallExists = functionsAndProperties.any { it.hasAnnotation(BackInTimeAnnotations.captureAnnotationClassId, session) }

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

package com.github.kitakkun.backintime.compiler.fir

import com.github.kitakkun.backintime.compiler.BackInTimeAnnotations
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.hasAnnotationSafe
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.constructClassType
import org.jetbrains.kotlin.fir.types.toLookupTag

class BackInTimeFirStateHolderManipulatorSuperTypeGenerationExtension(session: FirSession) : FirSupertypeGenerationExtension(session = session) {
    override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean {
        return declaration.hasAnnotationSafe(BackInTimeAnnotations.debuggableStateHolderAnnotationClassId, session)
    }

    context(TypeResolveServiceContainer)
    override fun computeAdditionalSupertypes(classLikeDeclaration: FirClassLikeDeclaration, resolvedSupertypes: List<FirResolvedTypeRef>): List<FirResolvedTypeRef> {
        return listOf(buildResolvedTypeRef {
            type = BackInTimeAnnotations.debuggableStateHolderManipulatorAnnotationClassId.toLookupTag().constructClassType(emptyArray(), false)
        })
    }
}

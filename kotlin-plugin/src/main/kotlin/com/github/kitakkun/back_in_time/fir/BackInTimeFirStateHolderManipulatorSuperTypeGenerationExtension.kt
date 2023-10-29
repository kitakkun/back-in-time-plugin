package com.github.kitakkun.back_in_time.fir

import com.github.kitakkun.back_in_time.annotations.DebuggableStateHolder
import com.github.kitakkun.back_in_time.annotations.DebuggableStateHolderManipulator
import org.jetbrains.kotlin.descriptors.runtime.structure.classId
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.constructClassType
import org.jetbrains.kotlin.fir.types.toLookupTag

class BackInTimeFirStateHolderManipulatorSuperTypeGenerationExtension(session: FirSession) : FirSupertypeGenerationExtension(session = session) {
    override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean {
        return declaration.hasAnnotation(DebuggableStateHolder::class.java.classId, session)
    }

    context(TypeResolveServiceContainer) override fun computeAdditionalSupertypes(classLikeDeclaration: FirClassLikeDeclaration, resolvedSupertypes: List<FirResolvedTypeRef>): List<FirResolvedTypeRef> {
        if (!classLikeDeclaration.hasAnnotation(DebuggableStateHolder::class.java.classId, session)) return emptyList()
        return listOf(buildResolvedTypeRef {
            type = DebuggableStateHolderManipulator::class.java.classId.toLookupTag().constructClassType(emptyArray(), false)
        })
    }
}

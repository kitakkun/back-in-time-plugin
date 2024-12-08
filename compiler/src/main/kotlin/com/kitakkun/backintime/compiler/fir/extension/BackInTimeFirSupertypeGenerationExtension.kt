package com.kitakkun.backintime.compiler.fir.extension

import com.kitakkun.backintime.compiler.consts.BackInTimeAnnotations
import com.kitakkun.backintime.compiler.consts.BackInTimeConsts
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.hasAnnotationSafe
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef

class BackInTimeFirSupertypeGenerationExtension(session: FirSession) : FirSupertypeGenerationExtension(session = session) {
    override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean {
        return declaration.hasAnnotationSafe(BackInTimeAnnotations.backInTimeAnnotationClassId, session)
    }

    override fun computeAdditionalSupertypes(classLikeDeclaration: FirClassLikeDeclaration, resolvedSupertypes: List<FirResolvedTypeRef>, typeResolver: TypeResolveService): List<FirResolvedTypeRef> {
        return listOf(
            buildResolvedTypeRef {
                type = BackInTimeConsts.backInTimeDebuggableInterfaceClassId.defaultType(emptyList())
            },
        )
    }
}

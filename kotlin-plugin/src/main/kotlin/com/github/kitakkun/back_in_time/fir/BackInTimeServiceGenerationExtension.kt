package com.github.kitakkun.back_in_time.fir

import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.builder.buildRegularClass
import org.jetbrains.kotlin.fir.declarations.impl.FirDeclarationStatusImpl
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class BackInTimeServiceGenerationExtension(
    session: FirSession
) : FirDeclarationGenerationExtension(session) {
    override fun generateTopLevelClassLikeDeclaration(classId: ClassId): FirClassLikeSymbol<*>? {
        return buildRegularClass {
            moduleData = session.moduleData
            status = FirDeclarationStatusImpl(visibility = Visibilities.Public, modality = null)
            name = Name.identifier("BackInTimeService")
            origin = FirDeclarationOrigin.Source
        }.symbol
    }

    override fun getTopLevelClassIds(): Set<ClassId> {
        return setOf(ClassId(FqName("com.github.kitakkun.back_in_time"), Name.identifier("BackInTimeService")))
    }
}

package com.kitakkun.backintime.compiler.k2.api

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.toRegularClassSymbol

object VersionSpecificAPIImpl : VersionSpecificAPI {
    override fun resolveToRegularClassSymbol(coneKotlinType: ConeKotlinType, session: FirSession): FirRegularClassSymbol? {
        return coneKotlinType.toRegularClassSymbol(session)
    }
}
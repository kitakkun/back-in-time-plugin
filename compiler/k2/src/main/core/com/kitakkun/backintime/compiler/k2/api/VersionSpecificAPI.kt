package com.kitakkun.backintime.compiler.k2.api

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType

interface VersionSpecificAPI {
    companion object {
        lateinit var INSTANCE: VersionSpecificAPI
    }

    fun resolveToRegularClassSymbol(coneKotlinType: ConeKotlinType, session: FirSession): FirRegularClassSymbol?
}
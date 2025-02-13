package com.kitakkun.backintime.tooling.core.usecase

import androidx.compose.runtime.staticCompositionLocalOf
import com.kitakkun.backintime.tooling.core.database.BackInTimeDatabaseImpl
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDatabase

val LocalDatabase = staticCompositionLocalOf<BackInTimeDatabase> { BackInTimeDatabaseImpl.instance }

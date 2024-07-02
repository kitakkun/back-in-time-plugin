package com.github.kitakkun.backintime.runtime

import com.github.kitakkun.backintime.runtime.internal.BackInTimeCompilerInternalApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

private var backInTimeDebugServiceInstance: BackInTimeDebugService? = null

@BackInTimeCompilerInternalApi
fun getBackInTimeDebugService(
    useInUnitTest: Boolean = false,
): BackInTimeDebugService {
    if (backInTimeDebugServiceInstance != null) return backInTimeDebugServiceInstance!!

    val dispatcher = if (useInUnitTest) Dispatchers.Unconfined else Dispatchers.IO
    backInTimeDebugServiceInstance = BackInTimeDebugServiceImpl(dispatcher)
    return backInTimeDebugServiceInstance!!
}

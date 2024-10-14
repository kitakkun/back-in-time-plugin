package io.github.kitakkun.backintime.core.runtime

import io.github.kitakkun.backintime.core.runtime.internal.BackInTimeCompilerInternalApi
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

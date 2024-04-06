package com.github.kitakkun.backintime.debugger.data.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

class IOScope : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob()
}

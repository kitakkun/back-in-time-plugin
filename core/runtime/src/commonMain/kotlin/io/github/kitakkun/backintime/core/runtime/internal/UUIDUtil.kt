package io.github.kitakkun.backintime.core.runtime.internal

import com.benasher44.uuid.uuid4

@BackInTimeCompilerInternalApi
fun uuid(): String = uuid4().toString()

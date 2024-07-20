package io.github.kitakkun.backintime.debugger.core.usecase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import io.github.kitakkun.backintime.debugger.core.model.ClassInfo
import io.github.kitakkun.backintime.debugger.core.model.PropertyInfo
import io.github.kitakkun.backintime.debugger.core.usecase.compositionlocal.localClassInfoRepository

@Composable
fun classInfo(
    sessionId: String,
    className: String,
): ClassInfo? {
    val classInfoRepository = localClassInfoRepository()
    val classInfoEntity by classInfoRepository.selectAsFlow(sessionId, className).collectAsState(null)

    if (classInfoEntity == null) return null

    val superClassInfo by rememberUpdatedState(
        classInfoEntity?.superClassName?.let { superClassName ->
            classInfo(sessionId, superClassName)
        }
    )

    return ClassInfo(
        name = className,
        properties = classInfoEntity!!.properties.map { entity ->
            PropertyInfo(
                name = entity.name,
                type = entity.type,
                debuggable = entity.debuggable,
                backInTimeDebuggable = entity.backInTimeDebuggable,
            )
        },
        superClass = superClassInfo,
    )
}

package io.github.kitakkun.backintime.debugger.core.data

import io.github.kitakkun.backintime.core.websocket.event.model.PropertyInfo
import io.github.kitakkun.backintime.debugger.core.database.dao.ClassInfoDao
import io.github.kitakkun.backintime.debugger.core.database.model.ClassInfoEntity
import io.github.kitakkun.backintime.debugger.core.database.model.PropertyInfoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull

/**
 * manage class information related to each session
 */
interface ClassInfoRepository {
    suspend fun insert(sessionId: String, className: String, superClassName: String, properties: List<PropertyInfo>)
    fun selectAsFlow(sessionId: String, className: String): Flow<ClassInfoEntity>
}

class ClassInfoRepositoryImpl(private val dao: ClassInfoDao) : ClassInfoRepository {
    override fun selectAsFlow(sessionId: String, className: String): Flow<ClassInfoEntity> {
        return dao.selectAsFlow(sessionId, className).filterNotNull()
    }

    override suspend fun insert(sessionId: String, className: String, superClassName: String, properties: List<PropertyInfo>) {
        dao.insert(
            ClassInfoEntity(
                sessionId = sessionId,
                name = className,
                properties = properties.map {
                    PropertyInfoEntity(
                        name = it.name,
                        type = it.valueType,
                        debuggable = it.debuggable,
                        backInTimeDebuggable = it.isDebuggableStateHolder,
                    )
                },
                superClassName = superClassName,
            )
        )
    }
}

package io.github.kitakkun.backintime.debugger.core.data

import io.github.kitakkun.backintime.debugger.core.database.dao.SessionInfoDao
import io.github.kitakkun.backintime.debugger.core.database.model.SessionInfoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

interface SessionInfoRepository {
    val allSessions: Flow<List<SessionInfoEntity>>
    val allConnectedSessions: Flow<List<SessionInfoEntity>>
    val allDisconnectedSessions: Flow<List<SessionInfoEntity>>
    suspend fun insert(sessionId: String, label: String? = null, startedAt: Long = Clock.System.now().toEpochMilliseconds())
    suspend fun select(sessionId: String): SessionInfoEntity?
    suspend fun markAsConnected(id: String)
    suspend fun markAsDisconnected(id: String)
    suspend fun markAllAsDisconnected()
}

class SessionInfoRepositoryImpl(private val dao: SessionInfoDao) : SessionInfoRepository {
    override val allSessions: Flow<List<SessionInfoEntity>> = dao.selectAllAsFlow()
    override val allConnectedSessions = dao.selectAllActiveAsFlow()
    override val allDisconnectedSessions = dao.selectAllInactiveAsFlow()

    override suspend fun markAllAsDisconnected() {
        dao.markAllAsInactive()
    }

    override suspend fun select(sessionId: String): SessionInfoEntity? = dao.select(sessionId)

    override suspend fun insert(sessionId: String, label: String?, startedAt: Long) {
        dao.insert(
            SessionInfoEntity(
                id = sessionId,
                label = label ?: sessionId,
                createdAt = startedAt,
                isActive = true,
            )
        )
    }

    override suspend fun markAsConnected(id: String) {
        dao.updateIsActive(id, true)
    }

    override suspend fun markAsDisconnected(id: String) {
        dao.updateIsActive(id, false)
    }
}

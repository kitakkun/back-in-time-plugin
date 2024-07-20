package io.github.kitakkun.backintime.debugger.core.data

import com.benasher44.uuid.uuid4
import io.github.kitakkun.backintime.debugger.core.database.dao.ValueChangeDao
import io.github.kitakkun.backintime.debugger.core.database.model.ValueChangeEntity
import kotlinx.coroutines.flow.Flow

interface ValueChangeRepository {
    fun selectAsFlow(callId: String, instanceId: String, sessionId: String): Flow<List<ValueChangeEntity>>
    fun selectByPropertyAsFlow(
        instanceId: String,
        sessionId: String,
        propertyId: String,
    ): Flow<List<ValueChangeEntity>>

    suspend fun insert(
        sessionId: String,
        instanceId: String,
        ownerClassName: String,
        methodCallId: String,
        propertyName: String,
        value: String,
    )
}

class ValueChangeRepositoryImpl(
    private val dao: ValueChangeDao,
) : ValueChangeRepository {
    override fun selectAsFlow(callId: String, instanceId: String, sessionId: String): Flow<List<ValueChangeEntity>> {
        return dao.selectAsFlow(callId, instanceId, sessionId)
    }

    override fun selectByPropertyAsFlow(
        instanceId: String,
        sessionId: String,
        propertyId: String,
    ): Flow<List<ValueChangeEntity>> {
        return dao.selectByPropertyAsFlow(
            instanceId = instanceId,
            sessionId = sessionId,
            propertyId = propertyId,
        )
    }

    override suspend fun insert(
        sessionId: String,
        instanceId: String,
        ownerClassName: String,
        methodCallId: String,
        propertyName: String,
        value: String,
    ) {
        dao.insert(
            valueChangeEntity = ValueChangeEntity(
                id = uuid4().toString(),
                sessionId = sessionId,
                instanceId = instanceId,
                methodCallId = methodCallId,
                propertyName = propertyName,
                propertyOwnerClassName = "",
                newValue = value,
            )
        )
    }
}

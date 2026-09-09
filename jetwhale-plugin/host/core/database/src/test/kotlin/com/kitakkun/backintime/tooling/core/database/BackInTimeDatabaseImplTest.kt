package com.kitakkun.backintime.tooling.core.database

import com.kitakkun.backintime.tooling.model.EventEntity
import kotlin.test.Test

class BackInTimeDatabaseImplTest {
    @Test
    fun test() {
        val database = BackInTimeDatabaseImpl.instance
        database.insert(EventEntity.Instance.Unregister(sessionId = "sessionId", instanceId = "uuid", time = 0L))
    }
}

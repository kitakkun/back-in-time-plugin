package com.kitakkun.backintime.tooling.database

import com.kitakkun.backintime.tooling.model.InstanceEventData
import kotlin.test.Test

class BackInTimeDatabaseImplTest {
    @Test
    fun test() {
        val database = BackInTimeDatabaseImpl()
        database.saveEvent("sessionId", InstanceEventData.Unregister(instanceId = "uuid", time = 0L))
    }
}

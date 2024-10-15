package io.github.kitakkun.backintime.debugger.core.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.kitakkun.backintime.debugger.core.database.dao.ClassInfoDao
import io.github.kitakkun.backintime.debugger.core.database.dao.EventLogDao
import io.github.kitakkun.backintime.debugger.core.database.dao.InstanceDao
import io.github.kitakkun.backintime.debugger.core.database.dao.MethodCallDao
import io.github.kitakkun.backintime.debugger.core.database.dao.SessionInfoDao
import io.github.kitakkun.backintime.debugger.core.database.dao.ValueChangeDao
import io.github.kitakkun.backintime.debugger.core.database.model.ClassInfoEntity
import io.github.kitakkun.backintime.debugger.core.database.model.EventLogEntity
import io.github.kitakkun.backintime.debugger.core.database.model.InstanceEntity
import io.github.kitakkun.backintime.debugger.core.database.model.MethodCallEntity
import io.github.kitakkun.backintime.debugger.core.database.model.PropertyInfoEntity
import io.github.kitakkun.backintime.debugger.core.database.model.SessionInfoEntity
import io.github.kitakkun.backintime.debugger.core.database.model.ValueChangeEntity

@Database(
    entities = [
        ClassInfoEntity::class,
        InstanceEntity::class,
        SessionInfoEntity::class,
        EventLogEntity::class,
        ValueChangeEntity::class,
        MethodCallEntity::class,
    ],
    version = 1
)
@TypeConverters(
    value = [
        PropertyInfoEntity.ListConverter::class,
        StringListConverter::class,
        BackInTimeDebugServiceEventConverter::class,
    ]
)
abstract class BackInTimeDatabase : RoomDatabase() {
    abstract fun sessionInfoDao(): SessionInfoDao
    abstract fun classInfoDao(): ClassInfoDao
    abstract fun instanceDao(): InstanceDao
    abstract fun methodCallDao(): MethodCallDao
    abstract fun eventLogDao(): EventLogDao
    abstract fun valueChangeDao(): ValueChangeDao
}

fun createBackInTimeDatabase(): BackInTimeDatabase {
    return Room.databaseBuilder<BackInTimeDatabase>("backintime.db")
        .setDriver(BundledSQLiteDriver())
        .build()
}

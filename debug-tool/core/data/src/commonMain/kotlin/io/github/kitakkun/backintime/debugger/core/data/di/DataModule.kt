package io.github.kitakkun.backintime.debugger.core.data.di

import io.github.kitakkun.backintime.debugger.core.data.ClassInfoRepository
import io.github.kitakkun.backintime.debugger.core.data.ClassInfoRepositoryImpl
import io.github.kitakkun.backintime.debugger.core.data.EventLogRepository
import io.github.kitakkun.backintime.debugger.core.data.EventLogRepositoryImpl
import io.github.kitakkun.backintime.debugger.core.data.InstanceRepository
import io.github.kitakkun.backintime.debugger.core.data.InstanceRepositoryImpl
import io.github.kitakkun.backintime.debugger.core.data.MethodCallRepository
import io.github.kitakkun.backintime.debugger.core.data.MethodCallRepositoryImpl
import io.github.kitakkun.backintime.debugger.core.data.SessionInfoRepository
import io.github.kitakkun.backintime.debugger.core.data.SessionInfoRepositoryImpl
import io.github.kitakkun.backintime.debugger.core.data.ValueChangeRepository
import io.github.kitakkun.backintime.debugger.core.data.ValueChangeRepositoryImpl
import org.koin.dsl.module

val dataModule = module {
    single<ClassInfoRepository> { ClassInfoRepositoryImpl(get()) }
    single<EventLogRepository> { EventLogRepositoryImpl(get()) }
    single<InstanceRepository> { InstanceRepositoryImpl(get()) }
    single<MethodCallRepository> { MethodCallRepositoryImpl(get()) }
    single<ValueChangeRepository> { ValueChangeRepositoryImpl(get()) }
    single<SessionInfoRepository> { SessionInfoRepositoryImpl(get()) }
}

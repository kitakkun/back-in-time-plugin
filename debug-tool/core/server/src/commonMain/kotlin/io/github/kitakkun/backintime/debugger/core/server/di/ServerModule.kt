package io.github.kitakkun.backintime.debugger.core.server.di

import io.github.kitakkun.backintime.debugger.core.server.BackInTimeDebuggerService
import io.github.kitakkun.backintime.debugger.core.server.BackInTimeDebuggerServiceImpl
import io.github.kitakkun.backintime.debugger.core.server.IncomingEventProcessor
import io.github.kitakkun.backintime.debugger.core.server.IncomingEventProcessorImpl
import io.github.kitakkun.backintime.websocket.server.BackInTimeWebSocketServer
import io.github.kitakkun.backintime.websocket.server.BackInTimeWebSocketServerImpl
import org.koin.dsl.module

val serverModule = module {
    single<BackInTimeDebuggerService> { BackInTimeDebuggerServiceImpl(get(), get(), get()) }
    factory<IncomingEventProcessor> { IncomingEventProcessorImpl(get(), get(), get(), get(), get()) }
    single<BackInTimeWebSocketServer> { BackInTimeWebSocketServerImpl() }
}

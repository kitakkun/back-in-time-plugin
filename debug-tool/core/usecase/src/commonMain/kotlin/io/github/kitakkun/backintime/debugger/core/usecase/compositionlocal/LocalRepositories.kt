package io.github.kitakkun.backintime.debugger.core.usecase.compositionlocal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import io.github.kitakkun.backintime.debugger.core.data.ClassInfoRepository
import io.github.kitakkun.backintime.debugger.core.data.EventLogRepository
import io.github.kitakkun.backintime.debugger.core.data.EventLogRepositoryImpl
import io.github.kitakkun.backintime.debugger.core.data.InstanceRepository
import io.github.kitakkun.backintime.debugger.core.data.MethodCallRepository
import io.github.kitakkun.backintime.debugger.core.data.SessionInfoRepository
import io.github.kitakkun.backintime.debugger.core.data.ValueChangeRepository
import org.koin.compose.koinInject
import kotlin.reflect.KClass

val LocalRepositories = compositionLocalOf<Map<KClass<*>, Any>> {
    error("No LocalRepository provided")
}

@Composable
fun localSessionInfoRepository(): SessionInfoRepository {
    return LocalRepositories.current[SessionInfoRepository::class] as SessionInfoRepository
}

@Composable
fun localInstanceRepository(): InstanceRepository {
    return LocalRepositories.current[InstanceRepository::class] as InstanceRepository
}

@Composable
fun localClassInfoRepository(): ClassInfoRepository {
    return LocalRepositories.current[ClassInfoRepository::class] as ClassInfoRepository
}

@Composable
fun localMethodCallRepository(): MethodCallRepository {
    return LocalRepositories.current[MethodCallRepository::class] as MethodCallRepository
}

@Composable
fun localValueChangeRepository(): ValueChangeRepository {
    return LocalRepositories.current[ValueChangeRepository::class] as ValueChangeRepository
}

@Composable
fun localEventLogRepository(): EventLogRepository {
    return LocalRepositories.current[EventLogRepository::class] as EventLogRepositoryImpl
}

@Composable
fun ProvideLocalRepositories(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalRepositories provides mapOf(
            SessionInfoRepository::class to koinInject<SessionInfoRepository>(),
            InstanceRepository::class to koinInject<InstanceRepository>(),
            ClassInfoRepository::class to koinInject<ClassInfoRepository>(),
            MethodCallRepository::class to koinInject<MethodCallRepository>(),
            ValueChangeRepository::class to koinInject<ValueChangeRepository>(),
            EventLogRepository::class to koinInject<EventLogRepository>(),
        )
    ) {
        content()
    }
}

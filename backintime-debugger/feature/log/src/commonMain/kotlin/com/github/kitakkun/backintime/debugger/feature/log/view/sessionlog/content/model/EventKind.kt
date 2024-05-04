package com.github.kitakkun.backintime.debugger.feature.log.view.sessionlog.content.model

import androidx.compose.runtime.Composable
import com.github.kitakkun.backintime.log.generated.resources.Res
import com.github.kitakkun.backintime.log.generated.resources.label_check_instance_alive
import com.github.kitakkun.backintime.log.generated.resources.label_error
import com.github.kitakkun.backintime.log.generated.resources.label_method_call
import com.github.kitakkun.backintime.log.generated.resources.label_ping
import com.github.kitakkun.backintime.log.generated.resources.label_register_instance
import com.github.kitakkun.backintime.log.generated.resources.label_register_relationship
import com.github.kitakkun.backintime.log.generated.resources.label_value_change
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

enum class EventKind(
    private val labelRes: StringResource,
) {
    REGISTER_INSTANCE(Res.string.label_register_instance),
    CHECK_INSTANCE_ALIVE_RESULT(Res.string.label_check_instance_alive),
    METHOD_CALL(Res.string.label_method_call),
    VALUE_CHANGE(Res.string.label_value_change),
    REGISTER_RELATIONSHIP(Res.string.label_register_relationship),
    ERROR(Res.string.label_error),
    PING(Res.string.label_ping),
    ;

    val label @Composable get() = stringResource(labelRes)

    companion object {
        fun fromEvent(event: BackInTimeDebugServiceEvent): EventKind = when (event) {
            is BackInTimeDebugServiceEvent.RegisterInstance -> REGISTER_INSTANCE
            is BackInTimeDebugServiceEvent.CheckInstanceAliveResult -> CHECK_INSTANCE_ALIVE_RESULT
            is BackInTimeDebugServiceEvent.Error -> ERROR
            is BackInTimeDebugServiceEvent.NotifyMethodCall -> METHOD_CALL
            is BackInTimeDebugServiceEvent.NotifyValueChange -> VALUE_CHANGE
            is BackInTimeDebugServiceEvent.Ping -> PING
            is BackInTimeDebugServiceEvent.RegisterRelationship -> REGISTER_RELATIONSHIP
        }
    }
}

package com.kitakkun.jetwhale.plugins.backintime.host

import androidx.compose.runtime.Composable
import com.kitakkun.backintime.tooling.app.BackInTimeDebuggerAppWithCompositionLocals
import com.kitakkun.backintime.tooling.app.BackInTimeDebuggerSettingsImpl
import com.kitakkun.backintime.tooling.app.PluginStateServiceImpl
import com.kitakkun.backintime.tooling.core.database.BackInTimeDatabaseImpl
import com.kitakkun.backintime.tooling.core.database.BackInTimeEventConverter
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDatabase
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerService
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerSettings
import com.kitakkun.backintime.tooling.model.EventEntity
import com.kitakkun.backintime.tooling.model.PluginState
import com.kitakkun.jetwhale.host.sdk.JetWhaleHostPluginUi
import com.kitakkun.jetwhale.host.sdk.JetWhaleMessagingHostPlugin
import com.kitakkun.jetwhale.host.sdk.get
import com.kitakkun.jetwhale.host.sdk.put
import com.kitakkun.jetwhale.plugins.backintime.protocol.AgentErrorOccurred
import com.kitakkun.jetwhale.plugins.backintime.protocol.ForceSetPropertyValue
import com.kitakkun.jetwhale.plugins.backintime.protocol.InstanceRegistered
import com.kitakkun.jetwhale.plugins.backintime.protocol.InstanceStateChanged
import com.kitakkun.jetwhale.plugins.backintime.protocol.MethodInvoked
import com.kitakkun.jetwhale.plugins.backintime.protocol.RelationshipRegistered
import com.kitakkun.jetwhale.protocol.messaging.JetWhaleMessageHandlers
import com.kitakkun.jetwhale.protocol.messaging.JetWhaleMessagingException
import com.kitakkun.jetwhale.protocol.messaging.request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * The back-in-time debugger as a JetWhale host plugin.
 *
 * One instance exists per debug session, which is what makes [sessionId] safe to mint here: the
 * event database keeps every session side by side, and a session is exactly as long-lived as this
 * object. Settings and UI state, by contrast, are meant to outlive a session, so they are mirrored
 * into the plugin's own persistent storage.
 */
internal class BackInTimeHostPlugin :
    JetWhaleMessagingHostPlugin(),
    JetWhaleHostPluginUi {

    private val sessionId: String = UUID.randomUUID().toString()

    private val database: BackInTimeDatabase = BackInTimeDatabaseImpl.instance
    private val converter = BackInTimeEventConverter()

    private val settings = BackInTimeDebuggerSettingsImpl()
    private val pluginStateService = PluginStateServiceImpl()

    private val debuggerService = object : BackInTimeDebuggerService {
        override fun backInTime(instanceId: String, values: Map<String, String>) {
            pluginScope.launch {
                // Record the rewind before issuing it: the agent reports the resulting property
                // changes as ordinary events, and they must not appear before the operation that
                // caused them.
                insert(
                    EventEntity.Instance.BackInTime(
                        sessionId = sessionId,
                        instanceId = instanceId,
                        time = System.currentTimeMillis(),
                        jsonValues = values,
                        destinationPointEventId = null,
                    ),
                )
                values.forEach { (propertySignature, jsonValue) ->
                    // One request per property: the agent applies each assignment through the
                    // instance's own setter, so they cannot be batched into a single write.
                    try {
                        messenger.request(
                            ForceSetPropertyValue(
                                targetInstanceId = instanceId,
                                propertySignature = propertySignature,
                                jsonValue = jsonValue,
                            ),
                        )
                    } catch (e: JetWhaleMessagingException) {
                        // Catch the messaging failure specifically — runCatching would also swallow
                        // the CancellationException that cancels this coroutine.
                        insert(
                            EventEntity.System.DebuggerError(
                                sessionId = sessionId,
                                time = System.currentTimeMillis(),
                                message = "Failed to rewind $propertySignature: ${e.message}",
                            ),
                        )
                    }
                }
            }
        }
    }

    override fun JetWhaleMessageHandlers.configure() {
        onEvent { event: InstanceRegistered -> insert(converter.convertToEntity(sessionId, event)) }
        onEvent { event: MethodInvoked -> insert(converter.convertToEntity(sessionId, event)) }
        onEvent { event: InstanceStateChanged -> insert(converter.convertToEntity(sessionId, event)) }
        onEvent { event: RelationshipRegistered -> insert(converter.convertToEntity(sessionId, event)) }
        onEvent { event: AgentErrorOccurred -> insert(converter.convertToEntity(sessionId, event)) }
    }

    override fun onCreate() {
        pluginScope.launch {
            val restored = storage.get<BackInTimeDebuggerSettings.State>(SETTINGS_KEY)
            if (restored != null) settings.update { restored }
            persist(SETTINGS_KEY, settings.stateFlow)
        }
        pluginScope.launch {
            val restored = storage.get<PluginState>(UI_STATE_KEY)
            if (restored != null) pluginStateService.loadState(restored)
            persist(UI_STATE_KEY, pluginStateService.stateFlow)
        }
    }

    @Composable
    override fun Content() {
        BackInTimeDebuggerAppWithCompositionLocals(
            sessionId = sessionId,
            debuggerService = debuggerService,
            settings = settings,
            pluginStateService = pluginStateService,
        )
    }

    /**
     * SQLDelight's JDBC driver is blocking, and a message handler that blocks holds one of the
     * peer's concurrent-request slots, so every write is moved off the handler's thread.
     */
    private suspend fun insert(eventEntity: EventEntity) {
        withContext(Dispatchers.IO) { database.insert(eventEntity) }
    }

    /**
     * Mirrors [flow] into this plugin's persistent storage.
     *
     * The first emission is the value just restored (or the default), so it is dropped rather than
     * written straight back. Writes are debounced because the UI state changes on every frame of a
     * split-pane drag.
     */
    private suspend inline fun <reified T> persist(key: String, flow: StateFlow<T>) {
        flow.drop(1).collectLatest { value ->
            delay(PERSIST_DEBOUNCE_MILLIS)
            storage.put(key, value)
        }
    }

    private companion object {
        const val SETTINGS_KEY = "settings"
        const val UI_STATE_KEY = "ui-state"
        const val PERSIST_DEBOUNCE_MILLIS = 300L
    }
}

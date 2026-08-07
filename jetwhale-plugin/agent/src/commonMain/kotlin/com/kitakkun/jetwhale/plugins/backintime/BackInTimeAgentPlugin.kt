package com.kitakkun.jetwhale.plugins.backintime

import com.kitakkun.backintime.core.runtime.event.BackInTimeDebuggableInstanceEvent
import com.kitakkun.jetwhale.agent.sdk.JetWhaleAgentPlugin
import com.kitakkun.jetwhale.agent.sdk.messaging.sendOrQueue
import com.kitakkun.jetwhale.plugins.backintime.protocol.AgentErrorOccurred
import com.kitakkun.jetwhale.plugins.backintime.protocol.CheckInstanceAlive
import com.kitakkun.jetwhale.plugins.backintime.protocol.ForceSetPropertyValue
import com.kitakkun.jetwhale.plugins.backintime.protocol.ForceSetPropertyValueResult
import com.kitakkun.jetwhale.plugins.backintime.protocol.InstanceAliveness
import com.kitakkun.jetwhale.plugins.backintime.protocol.InstanceRegistered
import com.kitakkun.jetwhale.plugins.backintime.protocol.InstanceStateChanged
import com.kitakkun.jetwhale.plugins.backintime.protocol.MethodInvoked
import com.kitakkun.jetwhale.plugins.backintime.protocol.RelationshipRegistered
import com.kitakkun.jetwhale.protocol.messaging.JetWhaleMessageHandlers
import com.kitakkun.jetwhale.protocol.messaging.reply

/**
 * The back-in-time plugin as it runs inside the app being debugged.
 *
 * Register the instance with the JetWhale agent runtime, then feed it every
 * [BackInTimeDebuggableInstanceEvent] the back-in-time runtime produces via [report].
 */
public class BackInTimeAgentPlugin(
    private val service: JetWhaleBackInTimeService,
) : JetWhaleAgentPlugin() {
    override val pluginId: String get() = PLUGIN_ID
    override val pluginVersion: String get() = PLUGIN_VERSION

    /**
     * Instance events are the whole point of the plugin and a debug session routinely starts before
     * the host is attached, so they are buffered rather than dropped while offline. The buffer is
     * bounded and drops the oldest entries when full: a history with a gap at the start is still
     * useful, whereas unbounded buffering would grow with the app's own activity.
     */
    override val offlineEventBufferCapacity: Int = 1024

    override fun JetWhaleMessageHandlers.configure() {
        onRequest { request: CheckInstanceAlive ->
            reply(InstanceAliveness(isAlive = service.getInstanceAliveness(request.instanceUUIDs)))
        }
        onRequest { request: ForceSetPropertyValue ->
            val applied = service.forceSetInstancePropertyValue(
                instanceUUID = request.targetInstanceId,
                propertySignature = request.propertySignature,
                jsonValue = request.jsonValue,
            )
            reply(ForceSetPropertyValueResult(applied = applied))
        }
    }

    /**
     * Forwards one back-in-time runtime event to the host.
     *
     * Events are queued while the host is disconnected (see [offlineEventBufferCapacity]) and
     * flushed, in order, on the next connection.
     */
    public fun report(event: BackInTimeDebuggableInstanceEvent) {
        when (event) {
            is BackInTimeDebuggableInstanceEvent.RegisterTarget -> messenger.sendOrQueue(
                InstanceRegistered(
                    instanceUUID = event.instance.backInTimeInstanceUUID,
                    classSignature = event.classSignature,
                    superClassSignature = event.superClassSignature,
                    properties = event.properties,
                    time = event.time,
                ),
            )

            is BackInTimeDebuggableInstanceEvent.RegisterRelationShip -> messenger.sendOrQueue(
                RelationshipRegistered(
                    parentUUID = event.parentInstance.backInTimeInstanceUUID,
                    childUUID = event.childInstance.backInTimeInstanceUUID,
                    time = event.time,
                ),
            )

            is BackInTimeDebuggableInstanceEvent.MethodCall -> messenger.sendOrQueue(
                MethodInvoked(
                    instanceUUID = event.instance.backInTimeInstanceUUID,
                    methodSignature = event.methodSignature,
                    methodCallUUID = event.methodCallId,
                    time = event.time,
                ),
            )

            is BackInTimeDebuggableInstanceEvent.PropertyValueChange -> messenger.sendOrQueue(
                InstanceStateChanged(
                    instanceUUID = event.instance.backInTimeInstanceUUID,
                    propertySignature = event.propertySignature,
                    jsonValue = event.propertyValue,
                    methodCallUUID = event.methodCallId,
                    time = event.time,
                ),
            )

            is BackInTimeDebuggableInstanceEvent.Error -> messenger.sendOrQueue(
                AgentErrorOccurred(
                    message = event.exception.message ?: event.exception.toString(),
                    time = event.time,
                ),
            )
        }
    }

    public companion object {
        /** Must match the `pluginId` the host plugin declares in its manifest, or the two never pair. */
        public const val PLUGIN_ID: String = "com.kitakkun.jetwhale.plugins.backintime"

        /** Checked against the host manifest's `agentVersionRange`. */
        public const val PLUGIN_VERSION: String = "1.0.0"
    }
}

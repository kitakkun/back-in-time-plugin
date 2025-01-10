@file:Suppress("UNUSED")

package com.kitakkun.backintime.tooling.flipper

import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import com.kitakkun.backintime.tooling.model.ClassInfo
import com.kitakkun.backintime.tooling.model.DependencyInfo
import com.kitakkun.backintime.tooling.model.InstanceInfo
import com.kitakkun.backintime.tooling.model.MethodCallInfo
import com.kitakkun.backintime.tooling.model.ValueChangeInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@JsExport
interface FlipperAppStateOwner {
    fun processEvent(jsonAppEvent: String)
    fun postDebuggerEvent(event: BackInTimeDebuggerEvent)
    fun updateTab(tab: FlipperTab)
    fun updateTabState(tabState: TabState)
    fun toggleNonDebuggablePropertyVisibility(visible: Boolean)
}

@JsExport
class FlipperAppStateOwnerImpl(
    @Suppress("NON_EXPORTABLE_TYPE")
    private val flipperClient: PluginClient<IncomingEvents, OutgoingEvents>,
    private val showNonDebuggableProperty: Atom<Boolean>,
) : FlipperAppStateOwner {
    private val mutableStateFlow = MutableStateFlow(FlipperAppState.Default)

    @Suppress("NON_EXPORTABLE_TYPE")
    val stateFlow = mutableStateFlow.asStateFlow()


    init {
        mutableStateFlow.update { it.copy(persistentState = it.persistentState.copy(showNonDebuggableProperty = showNonDebuggableProperty.get())) }

        flipperClient.onMessage("appEvent") {
            processEvent(it.payload)
        }
    }

    // Do not pass instances of event generated in the js-side! Type matching in when expressions for Js-generated events is not available.
    override fun processEvent(jsonAppEvent: String) {
        val event = BackInTimeDebugServiceEvent.fromJsonString(jsonAppEvent)
        mutableStateFlow.update { it.copy(events = it.events + event) }
        when (event) {
            is BackInTimeDebugServiceEvent.RegisterInstance -> {
                mutableStateFlow.update { appState ->
                    val superClassIsRegistered = appState.instanceInfoList.any { it.uuid == event.instanceUUID }
                    val newInstanceInfoList = if (superClassIsRegistered) {
                        appState.instanceInfoList.map { instanceInfo ->
                            if (instanceInfo.uuid == event.instanceUUID) instanceInfo.copy(classSignature = event.classSignature)
                            else instanceInfo
                        }
                    } else {
                        appState.instanceInfoList + InstanceInfo(event.instanceUUID, event.classSignature, true, event.registeredAt)
                    }

                    val newClassInfoList = if (appState.classInfoList.none { it.classSignature == event.classSignature }) {
                        appState.classInfoList + ClassInfo(classSignature = event.classSignature, superClassSignature = event.superClassSignature, properties = event.properties)
                    } else {
                        appState.classInfoList
                    }

                    appState.copy(
                        instanceInfoList = newInstanceInfoList,
                        classInfoList = newClassInfoList,
                    )
                }
            }

            is BackInTimeDebugServiceEvent.RegisterRelationship -> {
                mutableStateFlow.update { appState ->
                    val dependencyInfo = appState.dependencyInfoList.find { it.uuid == event.parentUUID }
                    if (dependencyInfo == null) {
                        appState.copy(
                            dependencyInfoList = appState.dependencyInfoList + DependencyInfo(event.parentUUID, listOf(event.childUUID))
                        )
                    } else {
                        appState.copy(
                            dependencyInfoList = appState.dependencyInfoList.map {
                                if (it.uuid == event.parentUUID) it.copy(dependsOn = it.dependsOn + event.childUUID)
                                else it
                            }
                        )
                    }
                }
            }

            is BackInTimeDebugServiceEvent.NotifyMethodCall -> {
                mutableStateFlow.update {
                    it.copy(
                        methodCallInfoList = it.methodCallInfoList +
                            MethodCallInfo(
                                callUUID = event.methodCallUUID,
                                instanceUUID = event.instanceUUID,
                                methodSignature = event.methodSignature,
                                calledAt = event.calledAt,
                                valueChanges = emptyList()
                            )
                    )
                }
            }

            is BackInTimeDebugServiceEvent.NotifyValueChange -> {
                mutableStateFlow.update { appState ->
                    appState.copy(
                        methodCallInfoList = appState.methodCallInfoList.map {
                            if (it.callUUID == event.methodCallUUID) {
                                it.copy(valueChanges = it.valueChanges + ValueChangeInfo(event.propertySignature, event.value))
                            } else {
                                it
                            }
                        }
                    )
                }
            }

            is BackInTimeDebugServiceEvent.CheckInstanceAliveResult -> {
                mutableStateFlow.update { appState ->
                    appState.copy(
                        instanceInfoList = appState.instanceInfoList.map {
                            it.copy(alive = event.isAlive[it.uuid] ?: it.alive)
                        }
                    )
                }
            }

            else -> {
                // do nothing
            }
        }
    }

    override fun postDebuggerEvent(event: BackInTimeDebuggerEvent) {
        // DON'T RENAME THIS VARIABLE!! It is referenced from the following js() call.
        val payload = BackInTimeDebuggerEvent.toJsonString(event)
        flipperClient.send("debuggerEvent", js("{payload: payload}"))
    }

    override fun updateTab(tab: FlipperTab) {
        mutableStateFlow.update { it.copy(activeTabIndex = tab) }
    }

    override fun updateTabState(tabState: TabState) {
        mutableStateFlow.update {
            it.copy(tabState = tabState)
        }
    }

    override fun toggleNonDebuggablePropertyVisibility(visible: Boolean) {
        showNonDebuggableProperty.set(visible)
        mutableStateFlow.update {
            it.copy(persistentState = it.persistentState.copy(showNonDebuggableProperty = showNonDebuggableProperty.get()))
        }
    }
}

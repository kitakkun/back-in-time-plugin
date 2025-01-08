// Read more: https://fbflipper.com/docs/tutorial/js-custom#creating-a-first-plugin
// API: https://fbflipper.com/docs/extending/flipper-plugin#pluginclient
import {createState, PluginClient} from "flipper-plugin";
import {IncomingEvents} from "./events/FlipperIncomingEvents";
import {OutgoingEvents} from "./events/FlipperOutgoingEvents";
import {configureStore, Dispatch, Store} from "@reduxjs/toolkit";
import {appActions, appReducer} from "./reducer/appReducer";
import {propertyInspectorReducer} from "./view/sidebar/property_inspector/propertyInspectorReducer";
import {rawEventLogReducer} from "./view/page/raw_logs/RawEventLogReducer";
import {valueEmitReducer} from "./view/page/value_emit/ValueEmitReducer";
import {editAndEmitValueReducer} from "./view/page/edited_value_emitter/EditAndEmitValueReducer";
import {AtomicPersistentState, initPersistentStateSlice, persistentStateReducer} from "./reducer/PersistentStateReducer";
import {rawLogInspectorReducer} from "./view/sidebar/raw_log_inspector/RawLogInspectorReducer";
import {backInTimeReducer} from "./view/page/backintime/BackInTimeReducer";
import {com} from "backintime-websocket-event";
import BackInTimeDebuggerEvent = com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent;
import BackInTimeDebugServiceEvent = com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent;

export default (client: PluginClient<IncomingEvents, OutgoingEvents>) => {
  initPersistentStateSlice(generatePersistentStates());

  const store = configurePluginStore();
  const dispatch = store.dispatch;

  client.onMessage("appEvent", (appEvent) => {
    const eventPayload: BackInTimeDebugServiceEvent = BackInTimeDebugServiceEvent.Companion.fromJsonString(appEvent.payload)
    dispatch(appActions.processEvent(eventPayload))
  })

  store.subscribe(() => {
    const pendingEvents = store.getState().app.pendingFlipperEventQueue as BackInTimeDebuggerEvent[];
    if (pendingEvents.length == 0) return;
    dispatch(appActions.clearPendingEventQueue());
    pendingEvents.forEach((event) => client.send("debuggerEvent", {
      payload: BackInTimeDebuggerEvent.Companion.toJsonString(event)
    }));
  })

  return {store};
}

function configurePluginStore(): Store {
  return configureStore({
    reducer: {
      app: appReducer,
      persistentState: persistentStateReducer(),
      rawEventLog: rawEventLogReducer,
      propertyInspector: propertyInspectorReducer,
      valueEmit: valueEmitReducer,
      editAndEmitValue: editAndEmitValueReducer,
      rawLogInspector: rawLogInspectorReducer,
      backInTime: backInTimeReducer,
    },
    middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware({
        serializableCheck: {
          ignoredActions: [
            "app/processEvent", 
            "rawLogInspector/open",
          ],
          ignoreState: true,
        },
      }),
  });
}

function generatePersistentStates(): AtomicPersistentState {
  return {
    showNonDebuggableProperty: createState(true, {persist: "DebuggerPreferences.showNonDebuggableProperty", persistToLocalStorage: true}),
  };
}

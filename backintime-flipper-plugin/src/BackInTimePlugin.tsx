// Read more: https://fbflipper.com/docs/tutorial/js-custom#creating-a-first-plugin
// API: https://fbflipper.com/docs/extending/flipper-plugin#pluginclient
import {createState, PluginClient} from "flipper-plugin";
import {IncomingEvents} from "./events/FlipperIncomingEvents";
import {isCheckInstanceAlive, isForceSetPropertyValue, OutgoingEvents} from "./events/FlipperOutgoingEvents";
import {configureStore, Dispatch, Store} from "@reduxjs/toolkit";
import {appActions, appReducer} from "./reducer/appReducer";
import {propertyInspectorReducer} from "./view/sidebar/property_inspector/propertyInspectorReducer";
import {rawEventLogReducer} from "./view/page/raw_logs/RawEventLogReducer";
import {valueEmitReducer} from "./view/page/value_emit/ValueEmitReducer";
import {editAndEmitValueReducer} from "./view/page/edited_value_emitter/EditAndEmitValueReducer";
import {AtomicPersistentState, initPersistentStateSlice, persistentStateReducer} from "./reducer/PersistentStateReducer";
import {rawLogInspectorReducer} from "./view/sidebar/raw_log_inspector/RawLogInspectorReducer";
import {backInTimeReducer} from "./view/page/backintime/BackInTimeReducer";
import {io} from "backintime-websocket-event";
import BackInTimeDebuggerEvent = io.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent;

export default (client: PluginClient<IncomingEvents, OutgoingEvents>) => {
  initPersistentStateSlice(generatePersistentStates());

  const store = configurePluginStore();
  const dispatch = store.dispatch;

  observeIncomingEvents(dispatch, client);
  observeOutgoingEvents(dispatch, store, client);

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
            "app/register",
            "app/registerRelationship",
            "app/registerMethodCall",
            "app/registerValueChange",
            "app/forceSetPropertyValue",
            "app/refreshInstanceAliveStatuses",
            "app/updateInstanceAliveStatuses",
          ]
        },
      }),
  });
}

function observeIncomingEvents(dispatch: Dispatch, client: PluginClient<IncomingEvents, OutgoingEvents>) {
  client.onMessage("register", (event) => dispatch(appActions.register(event)));
  client.onMessage("notifyValueChange", (event) => dispatch(appActions.registerValueChange(event)));
  client.onMessage("notifyMethodCall", (event) => dispatch(appActions.registerMethodCall(event)));
  client.onMessage("registerRelationship", (event) => dispatch(appActions.registerRelationship(event)));
  client.onMessage("checkInstanceAliveResult", (event) => dispatch(appActions.updateInstanceAliveStatuses(event)))
}

function observeOutgoingEvents(dispatch: Dispatch, store: Store, client: PluginClient<IncomingEvents, OutgoingEvents>) {
  store.subscribe(() => {
    const pendingEvents = store.getState().app.pendingFlipperEventQueue as BackInTimeDebuggerEvent[];
    if (pendingEvents.length == 0) return;
    dispatch(appActions.clearPendingEventQueue());
    pendingEvents.forEach((event) => processOutgoingEvent(client, event, dispatch));
  });
}

function processOutgoingEvent(client: PluginClient<IncomingEvents, OutgoingEvents>, event: BackInTimeDebuggerEvent, dispatch: Dispatch) {
  if (isForceSetPropertyValue(event)) {
    client.send("forceSetPropertyValue", event as BackInTimeDebuggerEvent.ForceSetPropertyValue);
  } else if (isCheckInstanceAlive(event)) {
    client.send("refreshInstanceAliveStatus", event as BackInTimeDebuggerEvent.CheckInstanceAlive);
  }
}

function generatePersistentStates(): AtomicPersistentState {
  return {
    showNonDebuggableProperty: createState(true, {persist: "DebuggerPreferences.showNonDebuggableProperty", persistToLocalStorage: true}),
  };
}

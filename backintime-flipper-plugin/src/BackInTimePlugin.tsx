// Read more: https://fbflipper.com/docs/tutorial/js-custom#creating-a-first-plugin
// API: https://fbflipper.com/docs/extending/flipper-plugin#pluginclient
import {createState, PluginClient} from "flipper-plugin";
import {IncomingEvents} from "./events/FlipperIncomingEvents";
import {isCheckInstanceAlive, isForceSetPropertyValue, OutgoingEvent, OutgoingEvents} from "./events/FlipperOutgoingEvents";
import {configureStore, Dispatch, Store} from "@reduxjs/toolkit";
import {appActions, appReducer} from "./reducer/appReducer";
import {propertyInspectorReducer} from "./view/sidebar/property_inspector/propertyInspectorReducer";
import {rawEventLogReducer} from "./view/page/raw_logs/RawEventLogReducer";
import {valueEmitReducer} from "./view/page/value_emit/ValueEmitReducer";
import {editAndEmitValueReducer} from "./view/page/edited_value_emitter/EditAndEmitValueReducer";
import {AtomicPersistentState, initPersistentStateSlice, persistentStateReducer} from "./reducer/PersistentStateReducer";
import {rawLogInspectorReducer} from "./view/sidebar/raw_log_inspector/RawLogInspectorReducer";
import {backInTimeReducer} from "./view/page/backintime/BackInTimeReducer";

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
  });
}

function observeIncomingEvents(dispatch: Dispatch, client: PluginClient<IncomingEvents, OutgoingEvents>) {
  client.onMessage("register", (event) => dispatch(appActions.register(event)));
  client.onMessage("notifyValueChange", (event) => dispatch(appActions.registerValueChange(event)));
  client.onMessage("notifyMethodCall", (event) => dispatch(appActions.registerMethodCall(event)));
  client.onMessage("registerRelationship", (event) => dispatch(appActions.registerRelationship(event)));
}

function observeOutgoingEvents(dispatch: Dispatch, store: Store, client: PluginClient<IncomingEvents, OutgoingEvents>) {
  store.subscribe(() => {
    const pendingEvents = store.getState().app.pendingFlipperEventQueue as OutgoingEvent[];
    if (pendingEvents.length == 0) return;
    dispatch(appActions.clearPendingEventQueue());
    pendingEvents.forEach((event) => processOutgoingEvent(client, event, dispatch));
  });
}

function processOutgoingEvent(client: PluginClient<IncomingEvents, OutgoingEvents>, event: OutgoingEvent, dispatch: Dispatch) {
  if (isForceSetPropertyValue(event)) {
    client.send("forceSetPropertyValue", event);
  } else if (isCheckInstanceAlive(event)) {
    client.send("refreshInstanceAliveStatus", event)
      .then((response) => dispatch(appActions.updateInstanceAliveStatuses(response)));
  }
}

function generatePersistentStates(): AtomicPersistentState {
  return {
    showNonDebuggableProperty: createState(true, {persist: "DebuggerPreferences.showNonDebuggableProperty", persistToLocalStorage: true}),
  };
}

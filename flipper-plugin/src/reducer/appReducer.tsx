import {createSelector, createSlice, PayloadAction} from "@reduxjs/toolkit";
import {InstanceInfo} from "../data/InstanceInfo";
import {MethodCallInfo} from "../data/MethodCallInfo";
import {DependencyInfo} from "../data/DependencyInfo";
import * as event from "backintime-websocket-event";
import * as model from "backintime-tooling-model";
import BackInTimeDebuggerEvent = event.com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent;
import BackInTimeDebugServiceEvent = event.com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent;
import BackInTimeWebSocketEvent = event.com.kitakkun.backintime.core.websocket.event.BackInTimeWebSocketEvent;
import ClassInfo = model.com.kitakkun.backintime.tooling.model.ClassInfo;

export interface AppState {
  activeTabIndex: string;

  events: BackInTimeWebSocketEvent[];

  // low level data obtains from flipper connection
  classInfoList: ClassInfo[];
  instanceInfoList: InstanceInfo[];
  methodCallInfoList: MethodCallInfo[];
  dependencyInfoList: DependencyInfo[];

  // to emit flipper events from everywhere
  pendingFlipperEventQueue: BackInTimeDebuggerEvent[];
}

const initialState: AppState = {
  activeTabIndex: '1',
  events: [],
  classInfoList: [],
  instanceInfoList: [],
  methodCallInfoList: [],
  dependencyInfoList: [],
  pendingFlipperEventQueue: [],
};

const appSlice = createSlice({
  name: "app",
  initialState: initialState,
  reducers: {
    saveEvent: (state, action: PayloadAction<BackInTimeWebSocketEvent>) => {
      state.events.push(action.payload)
    },
    register: (state, action: PayloadAction<BackInTimeDebugServiceEvent.RegisterInstance>) => {
      const event = action.payload;
      const existingInstanceInfo = state.instanceInfoList.find((info) => info.uuid == event.instanceUUID);
      // instance registration
      if (!existingInstanceInfo) {
        // if new instance is registered, add it to instance list
        state.instanceInfoList.push({
          uuid: event.instanceUUID,
          classSignature: event.classSignature,
          alive: true,
          registeredAt: event.registeredAt,
        });
      } else if (existingInstanceInfo.classSignature == event.superClassSignature) {
        // if instance is already registered, update its class name
        // because subclass is registered after superclass
        existingInstanceInfo.classSignature = event.classSignature
      }
      // classInfo registration
      const existingClassInfo = state.classInfoList.find((info) => info.classSignature == event.classSignature);
      if (existingClassInfo) return;
      state.classInfoList.push(new ClassInfo(
        event.classSignature,
        event.superClassSignature,
        // @ts-ignore
        event.properties,
      ));
    },
    registerRelationship: (state, action: PayloadAction<BackInTimeDebugServiceEvent.RegisterRelationship>) => {
      const existingDependencyInfo = state.dependencyInfoList.find((info) => info.uuid == action.payload.parentUUID);
      if (!existingDependencyInfo) {
        state.dependencyInfoList.push({
          uuid: action.payload.parentUUID,
          dependsOn: [action.payload.childUUID],
        });
      } else {
        state.dependencyInfoList.push({
          uuid: action.payload.parentUUID,
          dependsOn: [action.payload.childUUID, ...existingDependencyInfo.dependsOn],
        });
      }
    },
    registerMethodCall: (state, action: PayloadAction<BackInTimeDebugServiceEvent.NotifyMethodCall>) => {
      const event = action.payload;
      state.methodCallInfoList.push({
        callUUID: event.methodCallUUID,
        instanceUUID: event.instanceUUID,
        methodSignature: event.methodSignature,
        calledAt: event.calledAt,
        valueChanges: [],
      });
    },
    registerValueChange: (state, action: PayloadAction<BackInTimeDebugServiceEvent.NotifyValueChange>) => {
      const event = action.payload;
      const methodCallInfo = state.methodCallInfoList.find((info) => info.callUUID == event.methodCallUUID);
      if (!methodCallInfo) return;
      methodCallInfo.valueChanges.push({
        propertySignature: event.propertySignature,
        value: event.value,
      });
    },
    forceSetPropertyValue: (state, action: PayloadAction<BackInTimeDebuggerEvent.ForceSetPropertyValue>) => {
      state.pendingFlipperEventQueue.push(action.payload);
    },
    refreshInstanceAliveStatuses: (state, action: PayloadAction<BackInTimeDebuggerEvent.CheckInstanceAlive>) => {
      state.pendingFlipperEventQueue.push(action.payload);
    },
    clearPendingEventQueue: (state) => {
      state.pendingFlipperEventQueue = [];
    },
    updateInstanceAliveStatuses: (state, action: PayloadAction<BackInTimeDebugServiceEvent.CheckInstanceAliveResult>) => {
      Object.entries(action.payload.isAlive).forEach(([instanceUUID, alive]) => {
        const instanceInfo = state.instanceInfoList.find((info) => info.uuid == instanceUUID);
        if (!instanceInfo) return;
        instanceInfo.alive = alive;
      });
    },
    updateActiveTabIndex: (state, action) => {
      state.activeTabIndex = action.payload;
    },
  },
});

export const appActions = appSlice.actions;
export const appReducer = appSlice.reducer;

export const appStateSelector = (state: any) => state.app as AppState;
export const instanceInfoListSelector = createSelector(
  [appStateSelector],
  (state) => state.instanceInfoList
);
export const dependencyInfoListSelector = createSelector(
  [appStateSelector],
  (state) => state.dependencyInfoList
);
export const classInfoListSelector = createSelector(
  [appStateSelector],
  (state) => state.classInfoList
);
export const methodCallInfoListSelector = createSelector(
  [appStateSelector],
  (state) => state.methodCallInfoList
);

export const selectActiveTabIndex = (state: any) => state.app.activeTabIndex as string;

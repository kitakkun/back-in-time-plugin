import {createSelector, createSlice, PayloadAction} from "@reduxjs/toolkit";
import * as event from "backintime-websocket-event";
import * as model from "backintime-tooling-model";
import BackInTimeDebuggerEvent = event.com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent;
import BackInTimeDebugServiceEvent = event.com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent;
import BackInTimeWebSocketEvent = event.com.kitakkun.backintime.core.websocket.event.BackInTimeWebSocketEvent;
import ClassInfo = model.com.kitakkun.backintime.tooling.model.ClassInfo;
import DependencyInfo = model.com.kitakkun.backintime.tooling.model.DependencyInfo;
import {com, kotlin} from "backintime-tooling-model";
import KtList = kotlin.collections.KtList;
import InstanceInfo = com.kitakkun.backintime.tooling.model.InstanceInfo;
import MethodCallInfo = com.kitakkun.backintime.tooling.model.MethodCallInfo;
import ValueChangeInfo = com.kitakkun.backintime.tooling.model.ValueChangeInfo;

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
        state.instanceInfoList.push(new InstanceInfo(event.instanceUUID, event.classSignature, true, event.registeredAt));
      } else if (existingInstanceInfo.classSignature == event.superClassSignature) {
        // if instance is already registered, update its class name
        // because subclass is registered after superclass
        const index = state.instanceInfoList.findIndex((instanceInfo) => instanceInfo == existingInstanceInfo)
        state.instanceInfoList[index] = existingInstanceInfo.copy(undefined, event.classSignature)
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
        state.dependencyInfoList.push(
          new DependencyInfo(action.payload.parentUUID, KtList.fromJsArray([action.payload.childUUID]))
        )
      } else {
        state.dependencyInfoList.push(
          new DependencyInfo(action.payload.parentUUID, KtList.fromJsArray([action.payload.childUUID, ...existingDependencyInfo.dependsOn.asJsReadonlyArrayView()]))
        );
      }
    },
    registerMethodCall: (state, action: PayloadAction<BackInTimeDebugServiceEvent.NotifyMethodCall>) => {
      const event = action.payload;
      state.methodCallInfoList.push(new MethodCallInfo(event.methodCallUUID, event.instanceUUID, event.methodSignature, event.calledAt, KtList.fromJsArray<ValueChangeInfo>([])));
    },
    registerValueChange: (state, action: PayloadAction<BackInTimeDebugServiceEvent.NotifyValueChange>) => {
      const event = action.payload;
      const methodCallInfoIndex = state.methodCallInfoList.findIndex((info) => info.callUUID == event.methodCallUUID);
      if (methodCallInfoIndex == -1) return;
      state.methodCallInfoList[methodCallInfoIndex] = state.methodCallInfoList[methodCallInfoIndex].copy(
        undefined,
        undefined,
        undefined,
        undefined,
        KtList.fromJsArray<ValueChangeInfo>(
          [...state.methodCallInfoList[methodCallInfoIndex].valueChanges.asJsReadonlyArrayView(),
            new ValueChangeInfo(event.propertySignature, event.value)
          ]
        )
      )
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
        const instanceInfoIndex = state.instanceInfoList.findIndex((info) => info.uuid == instanceUUID);
        if (instanceInfoIndex == -1) return;
        state.instanceInfoList[instanceInfoIndex] = state.instanceInfoList[instanceInfoIndex].copy(undefined, undefined, alive)
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

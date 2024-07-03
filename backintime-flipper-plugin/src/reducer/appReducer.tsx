import {createSelector, createSlice, PayloadAction} from "@reduxjs/toolkit";
import {ClassInfo} from "../data/ClassInfo";
import {InstanceInfo} from "../data/InstanceInfo";
import {MethodCallInfo} from "../data/MethodCallInfo";
import {NotifyMethodCall, NotifyValueChange, RegisterInstance, RegisterRelationship} from "../events/FlipperIncomingEvents";
import {CheckInstanceAlive, CheckInstanceAliveResponse, ForceSetPropertyValue, OutgoingEvent} from "../events/FlipperOutgoingEvents";
import {DependencyInfo} from "../data/DependencyInfo";

export interface AppState {
  activeTabIndex: string;

  // low level data obtains from flipper connection
  classInfoList: ClassInfo[];
  instanceInfoList: InstanceInfo[];
  methodCallInfoList: MethodCallInfo[];
  dependencyInfoList: DependencyInfo[];

  // to emit flipper events from everywhere
  pendingFlipperEventQueue: OutgoingEvent[];
}

const initialState: AppState = {
  activeTabIndex: '1',
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
    register: (state, action: PayloadAction<RegisterInstance>) => {
      const event = action.payload;
      const existingInstanceInfo = state.instanceInfoList.find((info) => info.uuid == event.instanceUUID);
      // instance registration
      if (!existingInstanceInfo) {
        // if new instance is registered, add it to instance list
        state.instanceInfoList.push({
          uuid: event.instanceUUID,
          className: event.className,
          alive: true,
          registeredAt: event.registeredAt,
        });
      } else if (existingInstanceInfo.className == event.superClassName) {
        // if instance is already registered, update its class name
        // because subclass is registered after superclass
        existingInstanceInfo.className = event.className
      }
      // classInfo registration
      const existingClassInfo = state.classInfoList.find((info) => info.name == event.className);
      if (existingClassInfo) return;
      state.classInfoList.push({
        name: event.className,
        superClassName: event.superClassName,
        properties: event.properties.map((property) => (
          {
            name: property.name,
            type: property.propertyType,
            valueType: property.valueType,
            debuggable: property.debuggable,
            isDebuggableStateHolder: property.isDebuggableStateHolder,
          }
        )),
      });
    },
    registerRelationship: (state, action: PayloadAction<RegisterRelationship>) => {
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
    registerMethodCall: (state, action: PayloadAction<NotifyMethodCall>) => {
      const event = action.payload;
      state.methodCallInfoList.push({
        callUUID: event.methodCallUUID,
        instanceUUID: event.instanceUUID,
        methodName: event.methodName,
        calledAt: event.calledAt,
        valueChanges: [],
      });
    },
    registerValueChange: (state, action: PayloadAction<NotifyValueChange>) => {
      const event = action.payload;
      const methodCallInfo = state.methodCallInfoList.find((info) => info.callUUID == event.methodCallUUID);
      if (!methodCallInfo) return;
      methodCallInfo.valueChanges.push({
        propertyName: event.propertyName,
        value: event.value,
      });
    },
    forceSetPropertyValue: (state, action: PayloadAction<ForceSetPropertyValue>) => {
      state.pendingFlipperEventQueue.push(action.payload);
    },
    refreshInstanceAliveStatuses: (state, action: PayloadAction<CheckInstanceAlive>) => {
      state.pendingFlipperEventQueue.push(action.payload);
    },
    clearPendingEventQueue: (state) => {
      state.pendingFlipperEventQueue = [];
    },
    updateInstanceAliveStatuses: (state, action: PayloadAction<CheckInstanceAliveResponse>) => {
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
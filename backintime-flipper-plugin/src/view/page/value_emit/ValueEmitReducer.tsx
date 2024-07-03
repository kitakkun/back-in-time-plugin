import {createSelector, createSlice, PayloadAction} from "@reduxjs/toolkit";
import {classInfoListSelector, instanceInfoListSelector, methodCallInfoListSelector} from "../../../reducer/appReducer";
import {ValueEmitState} from "./ValueEmitView";

export interface ValueEmitNavArguments {
  instanceUUID: string;
  methodCallUUID: string;
}

interface ValueEmitReducerState {
  open: boolean;
  instanceUUID?: string;
  methodCallUUID?: string;
}

const initialState: ValueEmitReducerState = {
  open: false,
  instanceUUID: undefined,
  methodCallUUID: undefined,
};

const valueEmitSlice = createSlice({
  name: "valueEmit",
  initialState: initialState,
  reducers: {
    open: (state, action: PayloadAction<ValueEmitNavArguments>) => {
      state.instanceUUID = action.payload.instanceUUID;
      state.methodCallUUID = action.payload.methodCallUUID;
      state.open = true;
    },
    close: (state) => {
      state.instanceUUID = undefined;
      state.methodCallUUID = undefined;
      state.open = false;
    }
  },
});

export const valueEmitActions = valueEmitSlice.actions;
export const valueEmitReducer = valueEmitSlice.reducer;

const selectValueEmitState = (state: any) => state.valueEmit as ValueEmitReducerState;
export const valueEmitStateSelector = createSelector(
  [selectValueEmitState, classInfoListSelector, instanceInfoListSelector, methodCallInfoListSelector],
  (state, classInfoList, instanceInfoList, methodCallInfoList) => {
    const instanceInfo = instanceInfoList.find((info) => info.uuid == state.instanceUUID);
    const methodCallInfo = methodCallInfoList.find((info) => info.callUUID == state.methodCallUUID);
    const classInfo = classInfoList.find((info) => info.name == instanceInfo?.className);

    return {
      open: state.open,
      instanceInfo: instanceInfo,
      methodCallInfo: methodCallInfo,
      classInfo: classInfo,
    } as ValueEmitState;
  }
);

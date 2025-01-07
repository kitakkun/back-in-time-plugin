import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import {RawLogInspectorState} from "./RawLogInspectorView";
import {com} from "backintime-tooling-model";
import RawEventLog = com.kitakkun.backintime.tooling.model.RawEventLog;

const initialState: RawLogInspectorState = {
  selectedLog: null,
};

const rawLogInspectorSlice = createSlice({
  name: "rawLogInspector",
  initialState: initialState,
  reducers: {
    open: (state, action: PayloadAction<RawEventLog>) => {
      state.selectedLog = action.payload;
    },
  },
});

export const rawLogInspectorActions = rawLogInspectorSlice.actions;
export const rawLogInspectorReducer = rawLogInspectorSlice.reducer;

export const rawLogInspectorStateSelector = (state: any) => state.rawLogInspector as RawLogInspectorState;
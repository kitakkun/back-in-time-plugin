import {createSlice} from "@reduxjs/toolkit";
import {appActions} from "../../../reducer/appReducer";
import {RawEventLogState} from "./RawLogView";
import {com} from "backintime-tooling-model";
import RawEventLog = com.kitakkun.backintime.tooling.model.RawEventLog;

const initialState: RawEventLogState = {
  logs: [],
};

const rawEventLogSlice = createSlice({
  name: "rawEventLog",
  initialState: initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(appActions.register, (state, action) => {
        state.logs.push(new RawEventLog(crypto.randomUUID().toString(), new Date().toUTCString(), "register", action.payload));
      })
      .addCase(appActions.registerRelationship, (state, action) => {
        state.logs.push(new RawEventLog(crypto.randomUUID().toString(), new Date().toUTCString(), "registerRelationship", action.payload));
      })
      .addCase(appActions.updateInstanceAliveStatuses, (state, action) => {
        state.logs.push(new RawEventLog(crypto.randomUUID().toString(), new Date().toUTCString(), "updateInstanceAliveStatus", action.payload));
      })
      .addCase(appActions.registerValueChange, (state, action) => {
        state.logs.push(new RawEventLog(crypto.randomUUID().toString(), new Date().toUTCString(), "notifyValueChange", action.payload));
      })
      .addCase(appActions.registerMethodCall, (state, action) => {
        state.logs.push(new RawEventLog(crypto.randomUUID().toString(), new Date().toUTCString(), "notifyMethodCall", action.payload));
      });
  }
});

export const rawEventLogActions = rawEventLogSlice.actions;
export const rawEventLogReducer = rawEventLogSlice.reducer;

export const rawEventLogStateSelector = (state: any) => state.rawEventLog as RawEventLogState;

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
      .addCase(appActions.processEvent, (state, action) => {
        state.logs.push(new RawEventLog(crypto.randomUUID().toString(), new Date().toUTCString(), "register", action.payload));
      });
  }
});

export const rawEventLogActions = rawEventLogSlice.actions;
export const rawEventLogReducer = rawEventLogSlice.reducer;

export const rawEventLogStateSelector = (state: any) => state.rawEventLog as RawEventLogState;

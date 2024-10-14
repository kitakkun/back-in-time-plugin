import {createSlice, PayloadAction} from "@reduxjs/toolkit";

interface BackInTimeReducerState {
  open: boolean;
  instanceUUID?: string;
}

interface BackInTimeNavArguments {
  instanceUUID: string;
}

const initialState: BackInTimeReducerState = {
  open: false,
};

const backInTimeSlice = createSlice({
  name: "backInTime",
  initialState: initialState,
  reducers: {
    open: (state, action: PayloadAction<BackInTimeNavArguments>) => {
      state.instanceUUID = action.payload.instanceUUID;
      state.open = true;
    },
    close: (state) => {
      state.instanceUUID = undefined;
      state.open = false;
    },
  },
});

export const backInTimeActions = backInTimeSlice.actions;
export const backInTimeReducer = backInTimeSlice.reducer;

export const backInTimeReducerStateSelector = (state: any) => state.backInTime as BackInTimeReducerState;
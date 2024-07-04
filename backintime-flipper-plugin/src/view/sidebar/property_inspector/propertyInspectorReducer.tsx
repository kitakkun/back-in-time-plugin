import {createSlice, PayloadAction} from "@reduxjs/toolkit";

export interface PropertyInspectorReducerState {
  instanceUUID: string;
  propertyName: string;
}

const initialState: PropertyInspectorReducerState = {
  instanceUUID: "",
  propertyName: "",
}

export interface PropertyInspectorNavArguments {
  instanceUUID: string;
  propertyName: string;
}

const propertyInspectorSlice = createSlice({
  name: 'propertyInspector',
  initialState: initialState,
  reducers: {
    openPropertyInspector: (state, action: PayloadAction<PropertyInspectorNavArguments>) => {
      state.instanceUUID = action.payload.instanceUUID;
      state.propertyName = action.payload.propertyName;
    }
  }
});

export const propertyInspectorActions = propertyInspectorSlice.actions;
export const propertyInspectorReducer = propertyInspectorSlice.reducer;

export const propertyInspectorReducerStateSelector = (state: any) => state.propertyInspector as PropertyInspectorReducerState;

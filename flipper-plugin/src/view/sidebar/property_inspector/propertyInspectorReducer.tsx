import {createSlice, PayloadAction} from "@reduxjs/toolkit";

export interface PropertyInspectorReducerState {
  instanceUUID: string;
  propertySignature: string;
}

const initialState: PropertyInspectorReducerState = {
  instanceUUID: "",
  propertySignature: "",
}

export interface PropertyInspectorNavArguments {
  instanceUUID: string;
  propertySignature: string;
}

const propertyInspectorSlice = createSlice({
  name: 'propertyInspector',
  initialState: initialState,
  reducers: {
    openPropertyInspector: (state, action: PayloadAction<PropertyInspectorNavArguments>) => {
      state.instanceUUID = action.payload.instanceUUID;
      state.propertySignature = action.payload.propertySignature;
    }
  }
});

export const propertyInspectorActions = propertyInspectorSlice.actions;
export const propertyInspectorReducer = propertyInspectorSlice.reducer;

export const propertyInspectorReducerStateSelector = (state: any) => state.propertyInspector as PropertyInspectorReducerState;

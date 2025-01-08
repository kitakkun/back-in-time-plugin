import {createSlice, PayloadAction, Slice} from "@reduxjs/toolkit";
import {Atom} from "flipper-plugin";
import {com} from "backintime-tooling-model";
import PersistentState = com.kitakkun.backintime.tooling.model.ui.PersistentState;

export interface AtomicPersistentState {
  showNonDebuggableProperty: Atom<boolean>;
}

let persistentState: AtomicPersistentState;
let sliceInstance: Slice<PersistentState>;

export function initPersistentStateSlice(initialState: AtomicPersistentState) {
  persistentState = initialState;
  sliceInstance = createSlice({
    name: "persistentState",
    initialState: new PersistentState(persistentState.showNonDebuggableProperty.get()),
    reducers: {
      updateNonDebuggablePropertyVisibility: (state, action: PayloadAction<boolean>) => {
        state.showNonDebuggableProperty = action.payload;
        persistentState.showNonDebuggableProperty.set(action.payload);
      }
    }
  });
}

export const persistentStateActions = () => sliceInstance.actions;
export const persistentStateReducer = () => sliceInstance.reducer;

export const persistentStateSelector = (state: any) => state.persistentState as PersistentState;
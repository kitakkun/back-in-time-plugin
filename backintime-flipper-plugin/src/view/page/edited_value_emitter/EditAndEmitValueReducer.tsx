import {createSelector, createSlice, PayloadAction} from "@reduxjs/toolkit";
import {classInfoListSelector, instanceInfoListSelector} from "../../../reducer/appReducer";
import {EditAndEmitState} from "./EditAndEmitValueView";

interface EditAndEmitReducerState {
  initialValue: any;
  editingValue: any;
  open: boolean;
  instanceUUID: string;
  propertyName: string;
}

const initialState: EditAndEmitReducerState = {
  initialValue: undefined,
  editingValue: undefined,
  open: false,
  instanceUUID: "",
  propertyName: "",
};

interface EditAndEmitValueNavArgument {
  initialValue: any;
  instanceUUID: string;
  propertyName: string;
}

const editAndEmitValueSlice = createSlice({
  name: "editAndEmitValue",
  initialState: initialState,
  reducers: {
    open: (state, action: PayloadAction<EditAndEmitValueNavArgument>) => {
      state.initialValue = action.payload.initialValue;
      state.editingValue = action.payload.initialValue;
      state.instanceUUID = action.payload.instanceUUID;
      state.propertyName = action.payload.propertyName;
      state.open = true;
    },
    close: (state) => {
      state.open = false;
    },
    updateEditingValue: (state, action: PayloadAction<object>) => {
      state.editingValue = action.payload;
    },
  },
});

export const editAndEmitValueActions = editAndEmitValueSlice.actions;
export const editAndEmitValueReducer = editAndEmitValueSlice.reducer;

const editAndEmitValueReducerStateSelector = (state: any) => state.editAndEmitValue as EditAndEmitReducerState;
export const editAndEmitValueStateSelector = createSelector(
  [editAndEmitValueReducerStateSelector, classInfoListSelector, instanceInfoListSelector],
  (state, classInfoList, instanceInfoList) => {
    const instanceInfo = instanceInfoList.find((info) => info.uuid == state.instanceUUID);
    const classInfo = classInfoList.find((info) => info.name == instanceInfo?.className);
    const valueType = classInfo?.properties.find((property) => property.name == state.propertyName)?.valueType;

    return {
      ...state,
      valueType: valueType,
    } as EditAndEmitState;
  }
);
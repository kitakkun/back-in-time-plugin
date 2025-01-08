import {createSelector, createSlice, PayloadAction} from "@reduxjs/toolkit";
import {classInfoListSelector, instanceInfoListSelector} from "../../../reducer/appReducer";
import {EditAndEmitState} from "./EditAndEmitValueView";

interface EditAndEmitReducerState {
  initialValue: any;
  editingValue: any;
  open: boolean;
  instanceUUID: string;
  propertySignature: string;
}

const initialState: EditAndEmitReducerState = {
  initialValue: undefined,
  editingValue: undefined,
  open: false,
  instanceUUID: "",
  propertySignature: "",
};

interface EditAndEmitValueNavArgument {
  initialValue: any;
  instanceUUID: string;
  propertySignature: string;
}

const editAndEmitValueSlice = createSlice({
  name: "editAndEmitValue",
  initialState: initialState,
  reducers: {
    open: (state, action: PayloadAction<EditAndEmitValueNavArgument>) => {
      state.initialValue = action.payload.initialValue;
      state.editingValue = action.payload.initialValue;
      state.instanceUUID = action.payload.instanceUUID;
      state.propertySignature = action.payload.propertySignature;
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
    const classInfo = classInfoList.find((info) => info.classSignature == instanceInfo?.classSignature);
    const valueType = classInfo?.properties.asJsReadonlyArrayView().find((property) => property.signature == state.propertySignature)?.valueType;

    return {
      ...state,
      valueType: valueType,
    } as EditAndEmitState;
  }
);

import {useDispatch, useSelector} from "react-redux";
import {editAndEmitValueActions, editAndEmitValueStateSelector} from "./EditAndEmitValueReducer";
import {EditAndEmitValueView} from "./EditAndEmitValueView";
import React from "react";
import {Modal} from "antd";
import {appActions} from "../../../reducer/appReducer";
import {com} from "kmp-lib";
import BackInTimeDebuggerEvent = com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent;

export function EditAndEmitValueModalPage() {
  const state = useSelector(editAndEmitValueStateSelector);
  const dispatch = useDispatch();
  return <Modal
    centered={true}
    open={state.open}
    title={"Edit and Emit Value"}
    width={"80%"}
    cancelText={"Cancel"}
    okText={"Emit Edited Value"}
    onOk={() => {
      if (!state.instanceUUID || !state.propertyName || !state.valueType) return;
      const event = new BackInTimeDebuggerEvent.ForceSetPropertyValue(
        state.instanceUUID,
        state.propertyName,
        JSON.stringify(state.editingValue),
        state.valueType,
      );
      dispatch(appActions.forceSetPropertyValue(event));
      dispatch(editAndEmitValueActions.close());
    }}
    onCancel={() => dispatch(editAndEmitValueActions.close())}
  >
    <EditAndEmitValueView
      state={state}
      onEdit={(edit) => dispatch(editAndEmitValueActions.updateEditingValue(edit))}
    />
  </Modal>;
}
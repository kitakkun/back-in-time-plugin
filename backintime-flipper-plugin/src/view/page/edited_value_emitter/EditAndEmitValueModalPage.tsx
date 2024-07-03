import {useDispatch, useSelector} from "react-redux";
import {editAndEmitValueActions, editAndEmitValueStateSelector} from "./EditAndEmitValueReducer";
import {EditAndEmitValueView} from "./EditAndEmitValueView";
import React from "react";
import {Modal} from "antd";
import {appActions} from "../../../reducer/appReducer";

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
      dispatch(
        appActions.forceSetPropertyValue({
          instanceUUID: state.instanceUUID,
          propertyName: state.propertyName,
          value: JSON.stringify(state.editingValue),
          valueType: state.valueType,
        })
      );
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
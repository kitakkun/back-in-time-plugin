import {useDispatch, useSelector} from "react-redux";
import React from "react";
import {ValueEmitView} from "./ValueEmitView";
import {valueEmitActions, valueEmitStateSelector} from "./ValueEmitReducer";
import {Modal} from "antd";
import {editAndEmitValueActions} from "../edited_value_emitter/EditAndEmitValueReducer";
import {appActions} from "../../../reducer/appReducer";
import {EditAndEmitValueModalPage} from "../edited_value_emitter/EditAndEmitValueModalPage";
import {com} from "backintime-websocket-event";
import BackInTimeDebuggerEvent = com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent;

export function ValueEmitModalPage() {
  const state = useSelector(valueEmitStateSelector);
  const dispatch = useDispatch();

  return (
    <>
      <EditAndEmitValueModalPage/>
      <Modal
        centered={true}
        open={state.open}
        title={"Value Emitter"}
        footer={null}
        width={"80%"}
        onCancel={() => dispatch(valueEmitActions.close())}
      >
        <ValueEmitView
          state={state}
          onValueEmit={(propertyName: string, value: string) => {
            const instanceUUID = state.instanceInfo?.uuid;
            const valueType = state.classInfo?.properties.find((property) => property.name == propertyName)?.valueType;
            if (!instanceUUID || !valueType) {
              return;
            }
            const propertyFqName = `${state.classInfo?.name}.${propertyName}`;
            const event = new BackInTimeDebuggerEvent.ForceSetPropertyValue(instanceUUID, propertyFqName, value, valueType);
            dispatch(appActions.forceSetPropertyValue(event));
          }}
          onEditAndEmitValue={(propertyName: string, value: string) => {
            const instanceUUID = state.instanceInfo?.uuid;
            const valueType = state.classInfo?.properties.find((property) => property.name == propertyName)?.valueType;
            if (!instanceUUID || !valueType) {
              return;
            }
            const parsedValue = JSON.parse(value);
            dispatch(
              editAndEmitValueActions.open({
                instanceUUID: instanceUUID,
                propertyName: propertyName,
                initialValue: parsedValue,
              })
            );
          }}
        />
      </Modal>
    </>
  );
}

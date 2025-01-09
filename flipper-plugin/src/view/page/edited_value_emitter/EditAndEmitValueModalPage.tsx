import {EditAndEmitValueView} from "./EditAndEmitValueView";
import React, {useState} from "react";
import {Modal} from "antd";
import {com} from "backintime-flipper-lib";
import BackInTimeDebuggerEvent = com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent;
import FlipperAppStateOwner = com.kitakkun.backintime.tooling.flipper.FlipperAppStateOwner;

export interface EditAndEmitValueModalPageProps {
  instanceId: string
  callId: string
  propertySignature: string
  valueType: string
  initialValue: any
  onDismissRequest: () => void
}

export function EditAndEmitValueModalPage(props: EditAndEmitValueModalPageProps) {
  const [editingValue, setEditingValue] = useState()

  return <Modal
    centered={true}
    open={true}
    title={"Edit and Emit Value"}
    width={"80%"}
    cancelText={"Cancel"}
    okText={"Emit Edited Value"}
    onOk={() => {
      const event = new BackInTimeDebuggerEvent.ForceSetPropertyValue(
        props.instanceId,
        props.propertySignature,
        JSON.stringify(editingValue),
      );
      FlipperAppStateOwner.postDebuggerEvent(event)
      props.onDismissRequest()
    }}
    onCancel={props.onDismissRequest}
  >
    <EditAndEmitValueView
      initialValue={props.initialValue}
      editingValue={editingValue}
      instanceUUID={props.instanceId}
      valueType={props.valueType}
      propertySignature={props.propertySignature}
      onEdit={(edit) => setEditingValue(edit)}
    />
  </Modal>
}

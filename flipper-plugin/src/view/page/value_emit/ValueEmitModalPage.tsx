import React, {useState} from "react";
import {ValueEmitView} from "./ValueEmitView";
import {Modal} from "antd";
import {EditAndEmitValueModalPage, EditAndEmitValueModalPageProps} from "../edited_value_emitter/EditAndEmitValueModalPage";
import {com} from "backintime-flipper-lib";
import BackInTimeDebuggerEvent = com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent;
import selectValueEmitModalPageState = com.kitakkun.backintime.tooling.flipper.selector.selectValueEmitModalPageState;
import {useAppState} from "../../../context/LocalAppState";
import {useStateOwner} from "../../../context/StateOwnerContext";

interface ValueEmitModalPageProps {
  instanceId: string
  methodCallId: string
  onDismissRequest: () => void
}

export function ValueEmitModalPage(props: ValueEmitModalPageProps) {
  const appState = useAppState()
  const owner = useStateOwner()
  const state = selectValueEmitModalPageState(appState, props.instanceId, props.methodCallId)

  const [editAndEmitState, setEditAndEmitState] = useState<EditAndEmitValueModalPageProps | null>(null)

  return (
    <>
      {editAndEmitState &&
          <EditAndEmitValueModalPage
              initialValue={editAndEmitState.initialValue}
              instanceId={editAndEmitState.instanceId}
              callId={editAndEmitState.callId}
              valueType={editAndEmitState.valueType}
              propertySignature={editAndEmitState.propertySignature}
              onDismissRequest={editAndEmitState.onDismissRequest}
          />
      }
      <Modal
        centered={true}
        open={true}
        title={"Value Emitter"}
        footer={null}
        width={"80%"}
        onCancel={props.onDismissRequest}
      >
        <ValueEmitView
          instanceInfo={state.instanceInfo}
          methodCallInfo={state.methodCallInfo}
          classInfo={state.classInfo}
          onValueEmit={(propertySignature: string, value: string) => {
            const instanceUUID = state.instanceInfo?.uuid;
            const valueType = state.classInfo?.properties.asJsReadonlyArrayView().find((property) => property.signature == propertySignature)?.valueType;
            const classSignature = state.classInfo?.classSignature
            if (!instanceUUID || !valueType || !classSignature) {
              return;
            }
            const event = new BackInTimeDebuggerEvent.ForceSetPropertyValue(instanceUUID, propertySignature, value);
            owner.postDebuggerEvent(event)
          }}
          onEditAndEmitValue={(propertySignature: string, value: string) => {
            const valueType = state.classInfo?.properties.asJsReadonlyArrayView().find((property) => property.signature == propertySignature)?.valueType;
            if (!valueType) {
              return;
            }
            const parsedValue = JSON.parse(value);
            setEditAndEmitState({
              instanceId: props.instanceId,
              propertySignature: propertySignature,
              initialValue: parsedValue,
              valueType: valueType,
              callId: props.methodCallId,
              onDismissRequest: () => setEditAndEmitState(null)
            })
          }}
        />
      </Modal>
    </>
  );
}

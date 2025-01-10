import {Modal} from "antd";
import React from "react";
import {BackInTimeView} from "./BackInTimeView";
import {com} from "backintime-flipper-lib";
import selectBackInTimeState = com.kitakkun.backintime.tooling.flipper.selector.selectBackInTimeState;
import BackInTimeDebuggerEvent = com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent;
import {useAppState} from "../../../context/LocalAppState";
import {useStateOwner} from "../../../context/StateOwnerContext";
import selectBackInTimeValues = com.kitakkun.backintime.tooling.flipper.selector.selectBackInTimeValues;

interface BackInTimeModalPageProps {
  instanceId: string
  onDismissRequest: () => void
}

export function BackInTimeModalPage(props: BackInTimeModalPageProps) {
  const appState = useAppState()
  const owner = useStateOwner()
  const state = selectBackInTimeState(appState, true, props.instanceId)

  return (
    <>
      <Modal
        open={true}
        centered={true}
        title={"History Viewer"}
        width={"80%"}
        onCancel={props.onDismissRequest}
        footer={null}
      >
        <BackInTimeView
          state={state}
          onSelectHistory={(index) => {
            Modal.confirm({
              centered: true,
              content: "Are you sure to go back in time here?",
              onOk: () => {
                selectBackInTimeValues(state.histories, index).asJsReadonlyArrayView().forEach((info) => {
                  const event = new BackInTimeDebuggerEvent.ForceSetPropertyValue(
                    state.instanceUUID,
                    info.signature,
                    info.jsonValue,
                  );
                  owner.postDebuggerEvent(event)
                });
              },
            })
          }}
        />
      </Modal>
    </>
  );
}

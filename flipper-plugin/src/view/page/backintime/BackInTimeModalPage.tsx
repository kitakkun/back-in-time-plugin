import {Modal} from "antd";
import React from "react";
import {BackInTimeView} from "./BackInTimeView";
import * as model from "backintime-tooling-model";
import MethodCallHistoryInfo = model.com.kitakkun.backintime.tooling.model.ui.HistoryInfo.MethodCallHistoryInfo;
import {com} from "backintime-flipper-lib";
import selectBackInTimeState = com.kitakkun.backintime.tooling.flipper.selector.selectBackInTimeState;
import BackInTimeDebuggerEvent = com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent;
import FlipperAppStateOwner = com.kitakkun.backintime.tooling.flipper.FlipperAppStateOwner;

interface BackInTimeModalPageProps {
  instanceId: string
  onDismissRequest: () => void
}

export function BackInTimeModalPage(props: BackInTimeModalPageProps) {
  const state = selectBackInTimeState(true, props.instanceId)

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
                const methodCallHistories = state.histories.asJsReadonlyArrayView().slice(0, index + 1)
                  .filter((history) => history instanceof MethodCallHistoryInfo)
                  .map((history) => history as MethodCallHistoryInfo);
                const allValueChanges = methodCallHistories.flatMap((history) => history.valueChanges.asJsReadonlyArrayView());
                const propertyValueChanges = distinctBy(allValueChanges.reverse(), (valueChange) => valueChange.propertySignature);
                propertyValueChanges.forEach((info) => {
                  const event = new BackInTimeDebuggerEvent.ForceSetPropertyValue(
                    state.instanceUUID,
                    info.propertySignature,
                    info.value,
                  );
                  FlipperAppStateOwner.postDebuggerEvent(event)
                });
              },
            })
          }}
        />
      </Modal>
    </>
  );
}

function distinctBy<T, K>(array: T[], keySelector: (item: T) => K): T[] {
  const seen = new Set<K>();
  return array.filter(item => {
    const key = keySelector(item);
    if (seen.has(key)) {
      return false;
    }
    seen.add(key);
    return true;
  });
}

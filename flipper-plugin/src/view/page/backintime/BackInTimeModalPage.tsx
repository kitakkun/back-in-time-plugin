import {Modal} from "antd";
import React from "react";
import {useDispatch, useSelector} from "react-redux";
import {BackInTimeView} from "./BackInTimeView";
import {backInTimeStateSelector} from "./BackInTimeSelector";
import {appActions} from "../../../reducer/appReducer";
import {backInTimeActions} from "./BackInTimeReducer";
import * as event from "backintime-websocket-event";
import * as model from "backintime-tooling-model";
import BackInTimeDebuggerEvent = event.com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent;
import MethodCallHistoryInfo = model.com.kitakkun.backintime.tooling.model.ui.HistoryInfo.MethodCallHistoryInfo;

export function BackInTimeModalPage() {
  const state = useSelector(backInTimeStateSelector);
  const dispatch = useDispatch();

  return (
    <>
      <Modal
        open={state.open}
        centered={true}
        title={"History Viewer"}
        width={"80%"}
        onCancel={() => dispatch(backInTimeActions.close())}
        footer={null}
      >
        <BackInTimeView
          state={state}
          onSelectHistory={(index) => {
            Modal.confirm({
              centered: true,
              content: "Are you sure to go back in time here?",
              onOk: () => {
                const methodCallHistories = state.histories.slice(0, index + 1)
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
                  dispatch(appActions.processEvent(event));
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

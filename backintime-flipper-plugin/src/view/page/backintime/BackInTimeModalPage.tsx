import {Modal} from "antd";
import React from "react";
import {useDispatch, useSelector} from "react-redux";
import {BackInTimeView} from "./BackInTimeView";
import {backInTimeStateSelector} from "./BackInTimeSelector";
import {MethodCallHistoryInfo} from "./HistoryInfo";
import {appActions} from "../../../reducer/appReducer";
import {backInTimeActions} from "./BackInTimeReducer";

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
                  .filter((history) => history.title == "methodCall")
                  .map((history) => history as MethodCallHistoryInfo);
                const allValueChanges = methodCallHistories.flatMap((history) => history.valueChanges);
                const properties = distinctBy(allValueChanges.map((valueChange) => valueChange.propertyName), (name) => name);
                properties.forEach((name) => {
                  const value = allValueChanges.reverse().find((valueChange) => valueChange.propertyName == name)?.value;
                  if (!value) return;
                  dispatch(appActions.forceSetPropertyValue(
                    {
                      instanceUUID: state.instanceUUID,
                      propertyName: name,
                      value: value,
                      valueType: "", // 使われてないから大丈夫
                    }
                  ));
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

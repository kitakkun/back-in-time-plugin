import {createSelector} from "@reduxjs/toolkit";
import {backInTimeReducerStateSelector} from "./BackInTimeReducer";
import {instanceInfoListSelector, methodCallInfoListSelector} from "../../../reducer/appReducer";
import {HistoryInfo} from "./HistoryInfo";

export interface BackInTimeState {
  open: boolean;
  histories: HistoryInfo[];
  instanceUUID: string;
}

export const backInTimeStateSelector = createSelector(
  [backInTimeReducerStateSelector,
    instanceInfoListSelector,
    methodCallInfoListSelector,
  ],
  (reducerState, instanceInfoList, methodCallInfoList) => {
    const instanceInfo = instanceInfoList.find((info) => info.uuid == reducerState.instanceUUID);

    const registerEvent: HistoryInfo = {
      title: "register",
      timestamp: instanceInfo?.registeredAt ?? 0,
      subtitle: "uuid: " + instanceInfo?.uuid,
      description: instanceInfo?.classSignature ?? "",
    };

    const methodCallEvents: HistoryInfo[] = methodCallInfoList
      .filter((info) => info.instanceUUID == reducerState.instanceUUID)
      .map((info) => {
        return {
          title: "methodCall",
          timestamp: info.calledAt,
          subtitle: info.methodSignature,
          description: info.valueChanges.map((change) => `${change.propertySignature} = ${change.value}`).join(", "),
          valueChanges: info.valueChanges,
        } as HistoryInfo;
      });

    return {
      instanceUUID: reducerState.instanceUUID,
      open: reducerState.open,
      histories: [registerEvent, ...methodCallEvents]
    } as BackInTimeState;
  }
)

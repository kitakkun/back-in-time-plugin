import {createSelector} from "@reduxjs/toolkit";
import {backInTimeReducerStateSelector} from "./BackInTimeReducer";
import {instanceInfoListSelector, methodCallInfoListSelector} from "../../../reducer/appReducer";
import {com} from "backintime-tooling-model";
import HistoryInfo = com.kitakkun.backintime.tooling.model.ui.HistoryInfo;

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

    const registerEvent = new HistoryInfo.RegisterHistoryInfo(
      "uuid: " + instanceInfo?.uuid,
      instanceInfo?.registeredAt ?? 0,
      instanceInfo?.classSignature ?? "",
    );

    const methodCallEvents: HistoryInfo[] = methodCallInfoList
      .filter((info) => info.instanceUUID == reducerState.instanceUUID)
      .map((info) => {
        return new HistoryInfo.MethodCallHistoryInfo(
          info.methodSignature,
          info.calledAt,
          info.valueChanges.asJsReadonlyArrayView().map((change) => `${change.propertySignature} = ${change.value}`).join(", "),
          info.valueChanges,
        )
      });

    return {
      instanceUUID: reducerState.instanceUUID,
      open: reducerState.open,
      histories: [registerEvent, ...methodCallEvents]
    } as BackInTimeState;
  }
)

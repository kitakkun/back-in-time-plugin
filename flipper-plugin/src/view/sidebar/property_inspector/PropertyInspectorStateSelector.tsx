import {createSelector} from "@reduxjs/toolkit";
import {classInfoListSelector, instanceInfoListSelector, methodCallInfoListSelector} from "../../../reducer/appReducer";
import {PropertyInspectorState} from "./PropertyInspectorView";
import {propertyInspectorReducerStateSelector} from "./propertyInspectorReducer";
import * as model from "backintime-tooling-model";
import PropertyInfo = model.com.kitakkun.backintime.tooling.model.PropertyInfo;
import ClassInfo = model.com.kitakkun.backintime.tooling.model.ClassInfo;

export const propertyInspectorStateSelector = createSelector(
  [instanceInfoListSelector, classInfoListSelector, methodCallInfoListSelector, propertyInspectorReducerStateSelector],
  (instanceInfoList, classInfoList, methodCallInfoList, state) => {
    const instanceInfo = instanceInfoList.find((info) => info.uuid == state.instanceUUID);
    const propertyInfo = instanceInfo && getPropertiesRecursively(classInfoList, instanceInfo?.classSignature)
      .find((info) => info.signature == state?.propertySignature);

    const valueChanges = methodCallInfoList.filter((info) =>
      // FIXME: will not work correctly for the class which has a back-in-time debuggable class as a super class.
      info.instanceUUID == state.instanceUUID && info.valueChanges.asJsReadonlyArrayView().some((change) => change.propertySignature == state.propertySignature)
    ).map((info) => {
      return {
        methodCallUUID: info.callUUID,
        time: info.calledAt,
        // FIXME: will not work correctly for the class which has a back-in-time debuggable class as a super class.
        value: [...info.valueChanges.asJsReadonlyArrayView()].reverse().find((change) => change.propertySignature == state.propertySignature)?.value ?? "",
      }
    });

    return {
      instanceInfo: instanceInfo,
      propertyInfo: propertyInfo,
      valueChanges: valueChanges,
    } as PropertyInspectorState;
  }
);

function getPropertiesRecursively(classInfoList: ClassInfo[], classSignature: string): PropertyInfo[] {
  const classInfo = classInfoList.find((info) => info.classSignature == classSignature);
  if (!classInfo) return [];
  return [
    ...classInfo.properties.asJsReadonlyArrayView(),
    ...getPropertiesRecursively(classInfoList, classInfo.superClassSignature)
  ];
}

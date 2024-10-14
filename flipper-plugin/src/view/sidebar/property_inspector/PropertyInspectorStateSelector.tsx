import {createSelector} from "@reduxjs/toolkit";
import {classInfoListSelector, instanceInfoListSelector, methodCallInfoListSelector} from "../../../reducer/appReducer";
import {PropertyInspectorState} from "./PropertyInspectorView";
import {propertyInspectorReducerStateSelector} from "./propertyInspectorReducer";
import {ClassInfo, PropertyInfo} from "../../../data/ClassInfo";

export const propertyInspectorStateSelector = createSelector(
  [instanceInfoListSelector, classInfoListSelector, methodCallInfoListSelector, propertyInspectorReducerStateSelector],
  (instanceInfoList, classInfoList, methodCallInfoList, state) => {
    const instanceInfo = instanceInfoList.find((info) => info.uuid == state.instanceUUID);
    const propertyInfo = instanceInfo && getPropertiesRecursively(classInfoList, instanceInfo?.className)
      .find((info) => info.name == state?.propertyName);

    const valueChanges = methodCallInfoList.filter((info) =>
      // FIXME: will not work correctly for the class which has a back-in-time debuggable class as a super class.
      info.instanceUUID == state.instanceUUID && info.valueChanges.some((change) => change.propertyName.split(".").pop() == state.propertyName)
    ).map((info) => {
      return {
        methodCallUUID: info.callUUID,
        time: info.calledAt,
        // FIXME: will not work correctly for the class which has a back-in-time debuggable class as a super class.
        value: [...info.valueChanges].reverse().find((change) => change.propertyName.split(".").pop() == state.propertyName)?.value ?? "",
      }
    });

    return {
      instanceInfo: instanceInfo,
      propertyInfo: propertyInfo,
      valueChanges: valueChanges,
    } as PropertyInspectorState;
  }
);

function getPropertiesRecursively(classInfoList: ClassInfo[], className: string): PropertyInfo[] {
  const classInfo = classInfoList.find((info) => info.name == className);
  if (!classInfo) return [];
  return [
    ...classInfo.properties,
    ...getPropertiesRecursively(classInfoList, classInfo.superClassName)
  ];
}

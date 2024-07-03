import {classInfoListSelector, dependencyInfoListSelector, instanceInfoListSelector, methodCallInfoListSelector} from "../../../reducer/appReducer";
import {createSelector} from "@reduxjs/toolkit";
import {persistentStateSelector} from "../../../reducer/PersistentStateReducer";
import {InstanceItem, InstanceListState, PropertyItem} from "./InstanceListView";
import {ClassInfo} from "../../../data/ClassInfo";
import {ValueChangeInfo} from "../../../data/MethodCallInfo";
import {InstanceInfo} from "../../../data/InstanceInfo";
import {DependencyInfo} from "../../../data/DependencyInfo";

export const selectInstanceList = createSelector(
  [instanceInfoListSelector, classInfoListSelector, methodCallInfoListSelector, persistentStateSelector, dependencyInfoListSelector],
  (instanceInfoList, classInfoList, methodCallInfoList, persistentState, dependencyInfoList): InstanceListState => {
    const aliveInstance = instanceInfoList.filter((instance) => instance.alive)

    const instances = aliveInstance.map((instance) =>
      resolveInstanceInfo(
        classInfoList,
        instanceInfoList,
        dependencyInfoList,
        instance.className,
        instance.uuid,
        methodCallInfoList.filter((info) => info.instanceUUID == instance.uuid).flatMap((info) => info.valueChanges)),
    ).filter((instance) => instance != null) as InstanceItem[];

    return {
      instances: instances,
      showNonDebuggableProperty: persistentState.showNonDebuggableProperty,
    } as InstanceListState;
  }
);

function resolveInstanceInfo(
  classInfoList: ClassInfo[],
  instanceInfoList: InstanceInfo[],
  dependencyInfoList: DependencyInfo[],
  className: string,
  instanceUUID: string,
  allValueChangeEvents: ValueChangeInfo[],
): InstanceItem | undefined {
  const classInfo = classInfoList.find((info) => info.name == className);
  if (!classInfo) return;
  const superTypeInfo = resolveInstanceInfo(classInfoList, instanceInfoList, dependencyInfoList, classInfo.superClassName, instanceUUID, allValueChangeEvents);
  return {
    name: classInfo.name,
    superClassName: classInfo.superClassName,
    uuid: instanceUUID,
    properties: classInfo.properties.map((property) => {
      const dependingInstanceUUIDs = dependencyInfoList.find((info) => info.uuid == instanceUUID);
      const propertyInstanceInfo = dependingInstanceUUIDs?.dependsOn?.map((dependingInstanceUUID) =>
        instanceInfoList.find((info) => info.uuid == dependingInstanceUUID)
      )?.find((info) => info?.className == property.type);

      return {
        name: property.name,
        type: property.type,
        debuggable: property.debuggable,
        eventCount: allValueChangeEvents.filter((event) => event.propertyName == property.name).length,
        stateHolderInstance: propertyInstanceInfo && resolveInstanceInfo(
          classInfoList,
          instanceInfoList,
          dependencyInfoList,
          property.type,
          propertyInstanceInfo?.uuid,
          allValueChangeEvents,
        ),
      } as PropertyItem;
    }),
    superInstanceItem: superTypeInfo,
  };
}
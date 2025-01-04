import {classInfoListSelector, dependencyInfoListSelector, instanceInfoListSelector, methodCallInfoListSelector} from "../../../reducer/appReducer";
import {createSelector} from "@reduxjs/toolkit";
import {persistentStateSelector} from "../../../reducer/PersistentStateReducer";
import {InstanceItem, InstanceListState, PropertyItem} from "./InstanceListView";
import {ClassInfo} from "../../../data/ClassInfo";
import {ValueChangeInfo} from "../../../data/MethodCallInfo";
import {InstanceInfo} from "../../../data/InstanceInfo";
import {DependencyInfo} from "../../../data/DependencyInfo";
import {com} from "backintime-websocket-event";
import PropertyInfo = com.kitakkun.backintime.core.websocket.event.model.PropertyInfo;

export const selectInstanceList = createSelector(
  [instanceInfoListSelector, classInfoListSelector, methodCallInfoListSelector, persistentStateSelector, dependencyInfoListSelector],
  (instanceInfoList, classInfoList, methodCallInfoList, persistentState, dependencyInfoList): InstanceListState => {
    const aliveInstance = instanceInfoList.filter((instance) => instance.alive)

    const instances = aliveInstance.map((instance) =>
      resolveInstanceInfo(
        classInfoList,
        instanceInfoList,
        dependencyInfoList,
        instance.classSignature,
        instance.uuid,
        methodCallInfoList.filter((info) => info.instanceUUID == instance.uuid).flatMap((info) => info.valueChanges)),
    ).filter((instance) => instance != null) as InstanceItem[];

    return {
      instances: instances,
      showNonDebuggableProperty: persistentState.showNonDebuggableProperty,
    };
  }
);

function resolveInstanceInfo(
  classInfoList: ClassInfo[],
  instanceInfoList: InstanceInfo[],
  dependencyInfoList: DependencyInfo[],
  classSignature: string,
  instanceUUID: string,
  allValueChangeEvents: ValueChangeInfo[],
): InstanceItem | undefined {
  const classInfo = classInfoList.find((info) => info.classSignature == classSignature);
  if (!classInfo) return;
  const superTypeInfo = resolveInstanceInfo(classInfoList, instanceInfoList, dependencyInfoList, classInfo.superClassSignature, instanceUUID, allValueChangeEvents);
  return {
    name: classInfo.classSignature,
    superClassSignature: classInfo.superClassSignature,
    uuid: instanceUUID,
    properties: classInfo.properties.map((property) => {
      const dependingInstanceUUIDs = dependencyInfoList.find((info) => info.uuid == instanceUUID);
      const propertyInstanceInfo = dependingInstanceUUIDs?.dependsOn?.map((dependingInstanceUUID) =>
        instanceInfoList.find((info) => info.uuid == dependingInstanceUUID)
      )?.find((info) => info?.classSignature == property.propertyType);

      return {
        // @ts-ignore
        name: property.signature.split(".").at(-1).toString(),
        signature: property.signature,
        type: property.propertyType,
        debuggable: property.debuggable,
        eventCount: allValueChangeEvents.filter((event) => event.propertySignature == property.signature).length,
        stateHolderInstance: propertyInstanceInfo && resolveInstanceInfo(
          classInfoList,
          instanceInfoList,
          dependencyInfoList,
          property.propertyType,
          propertyInstanceInfo?.uuid,
          allValueChangeEvents,
        ),
      };
    }),
    superInstanceItem: superTypeInfo,
  };
}

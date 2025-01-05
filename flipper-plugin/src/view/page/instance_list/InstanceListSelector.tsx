import {classInfoListSelector, dependencyInfoListSelector, instanceInfoListSelector, methodCallInfoListSelector} from "../../../reducer/appReducer";
import {createSelector} from "@reduxjs/toolkit";
import {persistentStateSelector} from "../../../reducer/PersistentStateReducer";
import {InstanceItem, InstanceListState} from "./InstanceListView";
import {ValueChangeInfo} from "../../../data/MethodCallInfo";
import {InstanceInfo} from "../../../data/InstanceInfo";
import * as model from "backintime-tooling-model";
import ClassInfo = model.com.kitakkun.backintime.tooling.model.ClassInfo;
import DependencyInfo = model.com.kitakkun.backintime.tooling.model.DependencyInfo;

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
    properties: classInfo.properties.asJsReadonlyArrayView().map((property) => {
      const dependingInstanceUUIDs = dependencyInfoList.find((info) => info.uuid == instanceUUID);
      const propertyInstanceInfo = dependingInstanceUUIDs?.dependsOn?.asJsReadonlyArrayView().map((dependingInstanceUUID) =>
        instanceInfoList.find((info) => info.uuid == dependingInstanceUUID)
      )?.find((info) => info?.classSignature == property.propertyType);

      return {
        name: property.name,
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

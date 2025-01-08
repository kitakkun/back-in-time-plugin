import {classInfoListSelector, dependencyInfoListSelector, instanceInfoListSelector, methodCallInfoListSelector} from "../../../reducer/appReducer";
import {createSelector} from "@reduxjs/toolkit";
import {persistentStateSelector} from "../../../reducer/PersistentStateReducer";
import * as model from "backintime-tooling-model";
import ClassInfo = model.com.kitakkun.backintime.tooling.model.ClassInfo;
import DependencyInfo = model.com.kitakkun.backintime.tooling.model.DependencyInfo;
import {com, kotlin} from "backintime-tooling-model";
import InstanceInfo = com.kitakkun.backintime.tooling.model.InstanceInfo;
import ValueChangeInfo = com.kitakkun.backintime.tooling.model.ValueChangeInfo;
import InstanceItem = com.kitakkun.backintime.tooling.model.ui.InstanceItem;
import InstanceListState = com.kitakkun.backintime.tooling.model.ui.InstanceListState;
import KtList = kotlin.collections.KtList;
import PropertyItem = com.kitakkun.backintime.tooling.model.ui.PropertyItem;

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
        methodCallInfoList.filter((info) => info.instanceUUID == instance.uuid).flatMap((info) => info.valueChanges.asJsReadonlyArrayView())),
    ).filter((instance) => instance != null) as InstanceItem[];

    return new InstanceListState(KtList.fromJsArray(instances), persistentState.showNonDebuggableProperty);
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
  return new InstanceItem( 
    classInfo.classSignature,
    classInfo.superClassSignature,
    instanceUUID,
    KtList.fromJsArray(classInfo.properties.asJsReadonlyArrayView().map((property) => {
      const dependingInstanceUUIDs = dependencyInfoList.find((info) => info.uuid == instanceUUID);
      const propertyInstanceInfo = dependingInstanceUUIDs?.dependsOn?.asJsReadonlyArrayView().map((dependingInstanceUUID) =>
        instanceInfoList.find((info) => info.uuid == dependingInstanceUUID)
      )?.find((info) => info?.classSignature == property.propertyType);

      return new PropertyItem(
        property.name,
        property.signature,
        property.propertyType,
        property.debuggable,
        allValueChangeEvents.filter((event) => event.propertySignature == property.signature).length,
        propertyInstanceInfo && resolveInstanceInfo(
          classInfoList,
          instanceInfoList,
          dependencyInfoList,
          property.propertyType,
          propertyInstanceInfo?.uuid,
          allValueChangeEvents,
        ),
      )
    })),
    superTypeInfo,
  )
}

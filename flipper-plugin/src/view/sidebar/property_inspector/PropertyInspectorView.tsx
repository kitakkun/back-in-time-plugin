import React from "react";
import {InstanceDescriptionsView} from "./InstanceDescriptionsView";
import {PropertyDescriptionsView} from "./PropertyDescriptionsView";
import {PropertyValueChangesView} from "./PropertyValueChangesView";
import {Layout, theme} from "flipper-plugin";
import {com} from "backintime-tooling-model";
import PropertyInfo = com.kitakkun.backintime.tooling.model.PropertyInfo;
import InstanceInfo = com.kitakkun.backintime.tooling.model.InstanceInfo;

export interface ValueChangeInfo {
  methodCallUUID: string;
  time: number;
  value: string;
}

export interface PropertyInspectorState {
  instanceInfo: InstanceInfo | undefined;
  propertyInfo: PropertyInfo | undefined;
  valueChanges: ValueChangeInfo[];
}

type PropertyInspectorProps = {
  state: PropertyInspectorState;
  onClickValueChangeInfo: (methodCallUUID: string) => void;
}

export default function PropertyInspectorView(
  {
    state,
    onClickValueChangeInfo,
  }: PropertyInspectorProps
) {
  if (!state.instanceInfo || !state.propertyInfo) {
    return null;
  }
  return (
    <>
      <Layout.Container gap={theme.space.medium} pad={theme.inlinePaddingH}>
        <InstanceDescriptionsView instanceInfo={state.instanceInfo}/>
        <PropertyDescriptionsView propertyInfo={state.propertyInfo}/>
        <PropertyValueChangesView
          valueChanges={state.valueChanges}
          onClickRow={(methodCallUUID) => {
            onClickValueChangeInfo(methodCallUUID)
          }}/>
      </Layout.Container>
    </>
  );
}

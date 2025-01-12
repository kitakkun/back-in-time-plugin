import React from "react";
import {InstanceDescriptionsView} from "./InstanceDescriptionsView";
import {PropertyDescriptionsView} from "./PropertyDescriptionsView";
import {PropertyValueChangesView} from "./PropertyValueChangesView";
import {Layout, theme} from "flipper-plugin";
import {com} from "backintime-flipper-lib";
import PropertyInspectorState = com.kitakkun.backintime.tooling.model.ui.PropertyInspectorState;

type PropertyInspectorProps = {
  state: PropertyInspectorState;
  onClickValueChangeInfo: (methodCallUUID: string) => void;
}

export default function PropertyInspectorView(props: PropertyInspectorProps) {
  return (
    <>
      <Layout.Container gap={theme.space.medium} pad={theme.inlinePaddingH}>
        <InstanceDescriptionsView instanceInfo={props.state.instanceInfo}/>
        <PropertyDescriptionsView propertyInfo={props.state.propertyInfo}/>
        <PropertyValueChangesView
          valueChanges={props.state.valueChanges.asJsReadonlyArrayView()}
          onClickRow={(methodCallUUID) => {
            props.onClickValueChangeInfo(methodCallUUID)
          }}/>
      </Layout.Container>
    </>
  );
}

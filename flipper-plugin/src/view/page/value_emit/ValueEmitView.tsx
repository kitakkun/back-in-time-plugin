import {ChangedPropertiesView} from "./ChangedPropertiesView";
import React from "react";
import {Layout, theme} from "flipper-plugin";
import {MethodCallInfoView} from "./MethodCallInfoView";
import {com} from "backintime-flipper-lib";
import ClassInfo = com.kitakkun.backintime.tooling.model.ClassInfo;
import InstanceInfo = com.kitakkun.backintime.tooling.model.InstanceInfo;
import MethodCallInfo = com.kitakkun.backintime.tooling.model.MethodCallInfo;

type ValueEmitViewProps = {
  instanceInfo: InstanceInfo
  methodCallInfo: MethodCallInfo
  classInfo: ClassInfo
  onValueEmit: (propertyName: string, value: string) => void;
  onEditAndEmitValue: (propertyName: string, value: string) => void;
};

export function ValueEmitView(props: ValueEmitViewProps) {
  return (
    <Layout.Container padh={theme.inlinePaddingH} padv={theme.inlinePaddingV} gap={theme.space.medium} grow={true}>
      <MethodCallInfoView
        methodCallUUID={props.methodCallInfo.callUUID}
        instanceUUID={props.instanceInfo.uuid}
        calledAt={props.methodCallInfo.calledAt}
        methodName={props.methodCallInfo.methodSignature}
      />
      <ChangedPropertiesView
        classInfo={props.classInfo}
        methodCallInfo={props.methodCallInfo}
        onClickEmitValue={props.onValueEmit}
        onClickEditAndEmitValue={props.onEditAndEmitValue}
      />
    </Layout.Container>
  );
}
import {ChangedPropertiesView} from "./ChangedPropertiesView";
import React from "react";
import {Layout, theme} from "flipper-plugin";
import {MethodCallInfoView} from "./MethodCallInfoView";
import {InstanceInfo} from "../../../data/InstanceInfo";
import {MethodCallInfo} from "../../../data/MethodCallInfo";
import {ClassInfo} from "../../../data/ClassInfo";

type ValueEmitViewProps = {
  state: ValueEmitState,
  onValueEmit: (propertyName: string, value: string) => void;
  onEditAndEmitValue: (propertyName: string, value: string) => void;
};

export interface ValueEmitState {
  open: boolean;
  instanceInfo: InstanceInfo | null;
  methodCallInfo: MethodCallInfo | null;
  classInfo: ClassInfo | null;
}

export function ValueEmitView({state, onValueEmit, onEditAndEmitValue}: ValueEmitViewProps) {
  if (!state.instanceInfo || !state.methodCallInfo || !state.classInfo) {
    return null;
  }

  return (
    <Layout.Container padh={theme.inlinePaddingH} padv={theme.inlinePaddingV} gap={theme.space.medium} grow={true}>
      <MethodCallInfoView
        methodCallUUID={state.methodCallInfo?.callUUID}
        instanceUUID={state.instanceInfo.uuid}
        calledAt={state.methodCallInfo.calledAt}
        methodName={state.methodCallInfo.methodName}
      />
      <ChangedPropertiesView
        classInfo={state.classInfo}
        methodCallInfo={state.methodCallInfo}
        onClickEmitValue={onValueEmit}
        onClickEditAndEmitValue={onEditAndEmitValue}
      />
    </Layout.Container>
  );
}
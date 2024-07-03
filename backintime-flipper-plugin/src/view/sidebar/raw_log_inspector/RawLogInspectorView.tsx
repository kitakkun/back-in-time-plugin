import React from "react";
import {RawEventLog} from "../../../data/RawEventLog";
import {Typography} from "antd";
import {DataInspector, Layout, theme} from "flipper-plugin";

export interface RawLogInspectorState {
  selectedLog: RawEventLog | null;
}

export interface RawLogInspectorViewProps {
  state: RawLogInspectorState;
}

export function RawLogInspectorView({state}: RawLogInspectorViewProps) {
  return (
    <Layout.Container grow padh={theme.inlinePaddingH} padv={theme.inlinePaddingV}>
      <Typography.Title level={5}>Raw Log Inspector</Typography.Title>
      <Typography.Text>type: {state.selectedLog?.label}</Typography.Text>
      <DataInspector data={state.selectedLog?.payload}/>
    </Layout.Container>
  );
}

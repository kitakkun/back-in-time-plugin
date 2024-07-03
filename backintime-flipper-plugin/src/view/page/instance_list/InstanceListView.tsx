import React from "react";
import {Button, Switch} from "antd";
import {ReloadOutlined} from "@ant-design/icons";
import {Layout, theme} from "flipper-plugin";
import {InstanceTreeView} from "./InstanceTreeView";

export interface InstanceItem {
  name: string;
  uuid: string;
  superClassName: string;
  properties: PropertyItem[];
  superInstanceItem?: InstanceItem;
}

export interface PropertyItem {
  name: string;
  type: string;
  debuggable: boolean;
  eventCount: number;
  stateHolderInstance?: InstanceItem;
}

export interface InstanceListState {
  instances: InstanceItem[];
  showNonDebuggableProperty: boolean;
}

type InstanceListProps = {
  state: InstanceListState;
  onSelectProperty: (instanceUUID: string, propertyName: string) => void;
  onClickRefresh: () => void;
  onChangeNonDebuggablePropertyVisible: (visible: boolean) => void;
  onClickHistory: (instanceUUID: string) => void;
}

export function InstanceListView({state, onSelectProperty, onClickRefresh, onChangeNonDebuggablePropertyVisible, onClickHistory,}: InstanceListProps) {
  return <Layout.Container padv={theme.inlinePaddingV} padh={theme.inlinePaddingH} gap={theme.space.medium} grow={true}>
    <Layout.Horizontal gap={theme.space.medium} style={{display: "flex", alignItems: "center"}}>
      show non-debuggable properties:
      <Switch
        checked={state.showNonDebuggableProperty}
        onChange={(visible) => {
          onChangeNonDebuggablePropertyVisible(visible)
        }}
      />
      <Button onClick={onClickRefresh}>Refresh<ReloadOutlined/></Button>
    </Layout.Horizontal>
    <Layout.ScrollContainer>
      <InstanceTreeView
        instances={state.instances}
        onSelectProperty={onSelectProperty}
        onClickHistory={onClickHistory}
        showNonDebuggableProperty={state.showNonDebuggableProperty}
      />
    </Layout.ScrollContainer>
  </Layout.Container>;
}
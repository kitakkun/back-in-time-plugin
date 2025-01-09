import React from "react";
import {InstanceListPage} from "../page/instance_list/InstanceListPage";
import RawLogPage from "../page/raw_logs/RawLogPage";
import {Tabs} from "flipper-plugin";
import {TabsProps} from "antd";
import {com} from "backintime-flipper-lib";
import FlipperTab = com.kitakkun.backintime.tooling.flipper.FlipperTab;

type TabMenuProps = {
  activeKey: FlipperTab;
  onChange: (key: string) => void;
};

export function TabbedContent({activeKey, onChange}: TabMenuProps) {
  const items: TabsProps["items"] = [
    {
      key: FlipperTab.Instances.ordinal.toString(),
      label: 'Registered instances',
      children: <InstanceListPage/>
    },
    {
      key: FlipperTab.Logs.ordinal.toString(),
      label: 'Raw event log',
      children: <RawLogPage/>
    },
  ];
  return (
    <Tabs
      activeKey={activeKey.ordinal.toString()}
      defaultActiveKey={FlipperTab.Instances.ordinal.toString()}
      items={items}
      onChange={onChange}
      type={'card'}
      grow={true}
    />
  );
}
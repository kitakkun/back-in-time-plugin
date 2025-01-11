import React from "react";
import {InstanceListPage} from "../page/instance_list/InstanceListPage";
import RawLogPage from "../page/raw_logs/RawLogPage";
import {Tabs} from "flipper-plugin";
import {TabsProps} from "antd";
import {com} from "backintime-flipper-lib";
import FlipperTab = com.kitakkun.backintime.tooling.flipper.FlipperTab;

type TabMenuProps = {
  activeTab: FlipperTab;
  onChange: (selectedTab: FlipperTab) => void;
};

export function TabbedContent({activeTab, onChange}: TabMenuProps) {
  const items: TabsProps["items"] = [
    {
      key: FlipperTab.Instances.name.toString(),
      label: 'Registered instances',
      children: <InstanceListPage/>
    },
    {
      key: FlipperTab.Logs.name.toString(),
      label: 'Raw event log',
      children: <RawLogPage/>
    },
  ];
  return (
    <Tabs
      activeKey={activeTab.name.toString()}
      defaultActiveKey={FlipperTab.Instances.name.toString()}
      items={items}
      onChange={(key) => onChange(FlipperTab.valueOf(key))}
      type={'card'}
      grow={true}
    />
  );
}
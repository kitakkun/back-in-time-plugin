import {Descriptions} from "antd";
import React from "react";
import {com} from "backintime-tooling-model";
import InstanceInfo = com.kitakkun.backintime.tooling.model.InstanceInfo;

type InstanceInfoProps = {
  instanceInfo: InstanceInfo;
}

export function InstanceDescriptionsView({instanceInfo}: InstanceInfoProps) {
  return <Descriptions
    title={"Instance Info"}
    size={"small"}
    column={1}
    bordered
    layout={"horizontal"}
  >
    <Descriptions.Item label={"id"}>{instanceInfo.uuid}</Descriptions.Item>
    <Descriptions.Item label={"type"}>{instanceInfo.classSignature}</Descriptions.Item>
    <Descriptions.Item label={"registeredAt"}>{instanceInfo.registeredAt}</Descriptions.Item>
    <Descriptions.Item label={"alive"}>{instanceInfo.alive ? "true" : "false"}</Descriptions.Item>
  </Descriptions>;
}

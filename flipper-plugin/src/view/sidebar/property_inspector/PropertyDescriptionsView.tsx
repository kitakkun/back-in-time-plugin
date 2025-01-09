import {Descriptions} from "antd";
import React from "react";
import {com} from "backintime-tooling-model";
import PropertyInfo = com.kitakkun.backintime.tooling.model.PropertyInfo;

type PropertyInfoTableProps = {
  propertyInfo: PropertyInfo;
}

export function PropertyDescriptionsView({propertyInfo}: PropertyInfoTableProps) {
  return <Descriptions
    title={"Property Info"}
    size={"small"}
    column={1}
    bordered
    layout={"horizontal"}
  >
    <Descriptions.Item label={"name"}>{propertyInfo.signature}</Descriptions.Item>
    <Descriptions.Item label={"propertyType"}>{propertyInfo.propertyType}</Descriptions.Item>
    <Descriptions.Item label={"valueType"}>{propertyInfo.valueType}</Descriptions.Item>
    <Descriptions.Item label={"debuggable"}>{propertyInfo.debuggable ? "true" : "false"}</Descriptions.Item>
  </Descriptions>;
}

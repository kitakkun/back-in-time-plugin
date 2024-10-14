import {Descriptions, Table, Typography} from "antd";
import React from "react";
import {PropertyInfo} from "../../../data/ClassInfo";

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
    <Descriptions.Item label={"name"}>{propertyInfo.name}</Descriptions.Item>
    <Descriptions.Item label={"propertyType"}>{propertyInfo.type}</Descriptions.Item>
    <Descriptions.Item label={"valueType"}>{propertyInfo.valueType}</Descriptions.Item>
    <Descriptions.Item label={"debuggable"}>{propertyInfo.debuggable ? "true" : "false"}</Descriptions.Item>
  </Descriptions>;
}

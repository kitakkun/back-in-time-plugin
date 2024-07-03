import React from "react";
import {Table, Typography} from "antd";

type MethodCallInfoViewProps = {
  instanceUUID: string;
  methodName: string;
  methodCallUUID: string;
  calledAt: number;
}

export function MethodCallInfoView({instanceUUID, methodName, methodCallUUID, calledAt}: MethodCallInfoViewProps) {
  const dataSource = [
    {
      name: "instanceUUID",
      value: instanceUUID,
    },
    {
      name: "methodName",
      value: methodName,
    },
    {
      name: "methodCallUUID",
      value: methodCallUUID,
    },
    {
      name: "calledAt",
      value: calledAt,
    },
  ];

  const columns = [
    {
      title: 'name',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'value',
      dataIndex: 'value',
      key: 'value',
    },
  ];

  return (
    <>
      <Typography.Title level={5}>Method Call Info</Typography.Title>
      <Table dataSource={dataSource} columns={columns} showHeader={false} size={"small"} pagination={false}/>
    </>
  );
}
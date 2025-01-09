import {Table, Typography} from "antd";
import React from "react";
import {com} from "backintime-flipper-lib";
import ValueChangeInfo = com.kitakkun.backintime.tooling.model.ui.ValueChangeInfo;

type PropertyValueChangeTableProps = {
  valueChanges: readonly ValueChangeInfo[];
  onClickRow: (methodCallUUID: string) => void;
}

export function PropertyValueChangesView({valueChanges, onClickRow}: PropertyValueChangeTableProps) {
  const columns = [
    {
      title: 'Time',
      dataIndex: 'time',
      key: 'time',
    },
    {
      title: 'Value',
      dataIndex: 'value',
      key: 'value',
    },
  ];

  return <Table
    title={() => <Typography.Title level={5}>Value Changes</Typography.Title>}
    columns={columns}
    dataSource={valueChanges}
    scroll={{x: true}}
    size={"small"}
    onRow={(record) => {
      return {
        onClick: () => onClickRow(record.methodCallUUID)
      }
    }}
  />;
}

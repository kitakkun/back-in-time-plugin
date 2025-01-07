import React from "react";
import {Table, Typography} from "antd";
import {EmitButton} from "./EmitButton";
import {ColumnsType} from "antd/lib/table";
import {MyJsonView} from "../../component/MyJsonView";
import {com} from "backintime-tooling-model";
import ClassInfo = com.kitakkun.backintime.tooling.model.ClassInfo;
import MethodCallInfo = com.kitakkun.backintime.tooling.model.MethodCallInfo;

type ChangedPropertiesViewProps = {
  classInfo: ClassInfo;
  methodCallInfo: MethodCallInfo;
  onClickEmitValue: (propertyName: string, value: string) => void;
  onClickEditAndEmitValue: (propertyName: string, value: string) => void;
};

type ValueChangeItem = {
  action: React.ReactNode;
  name: string;
  value: any;
}

export function ChangedPropertiesView({classInfo, methodCallInfo, onClickEmitValue, onClickEditAndEmitValue}: ChangedPropertiesViewProps) {
  const dataSource: ValueChangeItem[] = methodCallInfo.valueChanges.asJsReadonlyArrayView().map((valueChange) => {
    // FIXME: will not work correctly for the class which has a back-in-time debuggable class as a super class.
    const property = classInfo.properties.asJsReadonlyArrayView().find((property) => property.signature === valueChange.propertySignature)!;
    const jsonValue = JSON.parse(valueChange.value);
    return {
      action: <EmitButton
        onClickEmitValue={() => onClickEmitValue(property.signature, valueChange.value)}
        onClickEditValue={() => onClickEditAndEmitValue(property.signature, valueChange.value)}
      />,
      name: property.signature,
      value: <MyJsonView initialValue={jsonValue} onEdit={null}/>
    };
  });

  const columns: ColumnsType<ValueChangeItem> = [
    {
      title: 'action',
      dataIndex: 'action',
      key: 'action',
      width: 100,
    },
    {
      title: 'name',
      dataIndex: 'name',
      key: 'name',
      width: 120,
    },
    {
      title: 'value',
      dataIndex: 'value',
      key: 'value',
    },
  ];

  return (
    <>
      <Typography.Title level={5}>Value Changes</Typography.Title>
      <Table dataSource={dataSource} columns={columns} scroll={{x: true}} size={"small"}/>
    </>
  );
}

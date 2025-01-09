import {Layout, theme} from "flipper-plugin";
import React from "react";
import {Typography} from "antd";
import {MyJsonView} from "../../component/MyJsonView";

interface EditAndEmitValueViewProps {
  initialValue: any;
  editingValue: any;
  instanceUUID: string;
  propertySignature: string;
  valueType: string | undefined;
  onEdit: (edit: any) => void;
}

export function EditAndEmitValueView(props : EditAndEmitValueViewProps) {
  return (
    <Layout.Horizontal gap={theme.space.medium}>
      {/* リテラルでJSONビュアーが表示されないFIX（もう1箇所ある）*/}
      <Layout.Container>
        <MyJsonView initialValue={props.initialValue} onEdit={props.onEdit}/>
      </Layout.Container>
      <Layout.Container>
        <Typography.Title level={5}>Property Info</Typography.Title>
        <Typography.Text>Instance UUID: {props.instanceUUID}</Typography.Text>
        <Typography.Text>Property Name: {props.propertySignature}</Typography.Text>
        <Typography.Text>Value Type: {props.valueType}</Typography.Text>

        Note that the value type can not be edited.
      </Layout.Container>
    </Layout.Horizontal>
  );
}

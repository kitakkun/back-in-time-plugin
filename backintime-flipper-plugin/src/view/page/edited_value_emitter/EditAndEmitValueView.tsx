import {Layout, theme} from "flipper-plugin";
import React from "react";
import {Typography} from "antd";
import {MyJsonView} from "../../component/MyJsonView";

export interface EditAndEmitState {
  initialValue: any;
  editingValue: any;
  open: boolean;
  instanceUUID: string;
  propertyName: string;
  valueType: string | undefined;
}

interface EditAndEmitValueViewProps {
  state: EditAndEmitState;
  onEdit: (edit: any) => void;
}

export function EditAndEmitValueView({state, onEdit}: EditAndEmitValueViewProps) {
  return (
    <Layout.Horizontal gap={theme.space.medium}>
      {/* リテラルでJSONビュアーが表示されないFIX（もう1箇所ある）*/}
      <Layout.Container>
        <MyJsonView initialValue={state.initialValue} onEdit={onEdit}/>
      </Layout.Container>
      <Layout.Container>
        <Typography.Title level={5}>Property Info</Typography.Title>
        <Typography.Text>Instance UUID: {state.instanceUUID}</Typography.Text>
        <Typography.Text>Property Name: {state.propertyName}</Typography.Text>
        <Typography.Text>Value Type: {state.valueType}</Typography.Text>

        Note that the value type can not be edited.
      </Layout.Container>
    </Layout.Horizontal>
  );
}
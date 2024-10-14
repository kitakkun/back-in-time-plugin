import {Button} from "antd";
import React from "react";
import {EditOutlined} from "@ant-design/icons";

type EmitButtonProps = {
  onClickEmitValue: () => void;
  onClickEditValue: () => void;
}

export function EmitButton({onClickEmitValue, onClickEditValue}: EmitButtonProps) {
  return (
    <Button.Group>
      <Button type="primary" onClick={onClickEmitValue}>Emit Value</Button>
      <Button type="primary" onClick={onClickEditValue}><EditOutlined/></Button>
    </Button.Group>
  )
}
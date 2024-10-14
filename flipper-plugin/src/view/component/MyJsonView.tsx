import ReactJson from "@microlink/react-json-view";
import {Input} from "antd";
import React from "react";

interface MyJsonViewProps {
  initialValue: any;
  onEdit: ((edit: any) => void) | null;
}

export function MyJsonView({initialValue, onEdit}: MyJsonViewProps) {
  const jsonEditorAvailable = typeof initialValue == "object" && initialValue != null;

  return <>
    {jsonEditorAvailable &&
        <ReactJson
            name={null}
            src={initialValue}
            theme={"rjv-default"}
            onEdit={(edit) => {
              if (typeof edit.new_value != typeof edit.existing_value) {
                return false;
              } else {
                onEdit && onEdit(edit.updated_src)
                return true;
              }
            }}
        />
    }
    {
      !jsonEditorAvailable &&
      (onEdit ? <Input defaultValue={JSON.stringify(initialValue)} onChange={(e) => onEdit && onEdit(e.target.value)}/> : JSON.stringify(initialValue))
    }
  </>
}
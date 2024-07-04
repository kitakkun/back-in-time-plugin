import {useSelector} from "react-redux";
import {rawLogInspectorStateSelector} from "./RawLogInspectorReducer";
import React from "react";
import {RawLogInspectorView} from "./RawLogInspectorView";

export function RawLogInspectorPage() {
  const state = useSelector(rawLogInspectorStateSelector);

  return <RawLogInspectorView state={state}/>;
}
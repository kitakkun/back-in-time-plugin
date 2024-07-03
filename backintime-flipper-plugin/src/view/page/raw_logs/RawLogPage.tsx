import React from "react";
import {useDispatch, useSelector} from "react-redux";
import {RawLogView} from "./RawLogView";
import {rawEventLogStateSelector} from "./RawEventLogReducer";
import {rawLogInspectorActions} from "../../sidebar/raw_log_inspector/RawLogInspectorReducer";

export default function RawLogPage() {
  const state = useSelector(rawEventLogStateSelector);
  const dispatch = useDispatch();

  return <RawLogView
    state={state}
    onSelectLog={(log) => dispatch(rawLogInspectorActions.open(log))}
  />;
}

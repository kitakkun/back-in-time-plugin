import React from "react";
import {RawLogView} from "./RawLogView";
import {com} from "backintime-flipper-lib";
import selectRawLogState = com.kitakkun.backintime.tooling.flipper.selector.selectRawLogState;

export default function RawLogPage() {
  const state = selectRawLogState()

  return <RawLogView
    state={state}
    onSelectLog={() => {
    }}
    // onSelectLog={(log) => dispatch(rawLogInspectorActions.open(log))}
  />;
}

import React from "react";
import {RawLogView} from "./RawLogView";
import {com} from "backintime-flipper-lib";
import selectRawLogState = com.kitakkun.backintime.tooling.flipper.selector.selectRawLogState;
import {useAppState} from "../../../context/LocalAppState";

export default function RawLogPage() {
  const appState = useAppState()
  const state = selectRawLogState(appState)

  return <RawLogView
    state={state}
    onSelectLog={() => {
    }}
    // onSelectLog={(log) => dispatch(rawLogInspectorActions.open(log))}
  />;
}

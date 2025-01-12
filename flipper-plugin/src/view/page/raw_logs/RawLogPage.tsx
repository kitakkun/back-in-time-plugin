import React from "react";
import {RawLogView} from "./RawLogView";
import {com} from "backintime-flipper-lib";
import selectRawLogState = com.kitakkun.backintime.tooling.flipper.selector.selectRawLogState;
import {useAppState} from "../../../context/LocalAppState";
import {useStateOwner} from "../../../context/StateOwnerContext";
import TabState = com.kitakkun.backintime.tooling.flipper.TabState;

export default function RawLogPage() {
  const owner = useStateOwner()
  const appState = useAppState()
  const state = selectRawLogState(appState)

  return <RawLogView
    state={state}
    onSelectLog={(log) => {
      owner.updateTabState(new TabState.LogTabState(log.eventId))
    }}
  />;
}

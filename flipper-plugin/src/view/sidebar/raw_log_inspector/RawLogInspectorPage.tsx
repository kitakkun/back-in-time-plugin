import React from "react";
import {RawLogInspectorView} from "./RawLogInspectorView";
import {useAppState} from "../../../context/LocalAppState";
import {com} from "backintime-flipper-lib";
import selectRawLogState = com.kitakkun.backintime.tooling.flipper.selector.selectRawLogState;

interface RawLogInspectorPageProps {
  selectedEventId: string
}

export function RawLogInspectorPage(props: RawLogInspectorPageProps) {
  const appState = useAppState()
  const rawLogs = selectRawLogState(appState)
  const selectedLog = rawLogs.logs.asJsReadonlyArrayView().find((log) => log.eventId == props.selectedEventId)
  return <RawLogInspectorView selectedLog={selectedLog}/>;
}
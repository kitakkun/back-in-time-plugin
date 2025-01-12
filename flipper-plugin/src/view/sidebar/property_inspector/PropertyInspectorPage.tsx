import React, {useState} from "react";
import PropertyInspectorView from "./PropertyInspectorView";
import {ValueEmitModalPage} from "../../page/value_emit/ValueEmitModalPage";
import {com} from "backintime-flipper-lib";
import selectPropertyInspectorState = com.kitakkun.backintime.tooling.flipper.selector.selectPropertyInspectorState;
import {useAppState} from "../../../context/LocalAppState";

interface PropertyInspectorPageProps {
  instanceId: string
  propertySignature: string
}

export function PropertyInspectorPage(props: PropertyInspectorPageProps) {
  const appState = useAppState()
  const state = selectPropertyInspectorState(appState, props.instanceId, props.propertySignature)

  if (!state) return

  const [valueEmitTargetMethodCallId, setValueEmitTargetMethodCallId] = useState<string | null>(null)

  return <>
    {
      valueEmitTargetMethodCallId &&
        <ValueEmitModalPage
            instanceId={props.instanceId}
            methodCallId={valueEmitTargetMethodCallId}
            onDismissRequest={() => setValueEmitTargetMethodCallId(null)}
        />
    }
    <PropertyInspectorView
      state={state}
      onClickValueChangeInfo={(methodCallUUID) => {
        if (!state.instanceInfo) return;
        setValueEmitTargetMethodCallId(methodCallUUID)
      }}
    />
  </>;
}
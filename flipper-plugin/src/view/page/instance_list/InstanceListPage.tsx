import React, {useState} from "react";
import {InstanceListView} from "./InstanceListView";
import {com, kotlin} from "backintime-flipper-lib";
import selectInstanceListState = com.kitakkun.backintime.tooling.flipper.selector.selectInstanceListState;
import {BackInTimeModalPage} from "../backintime/BackInTimeModalPage";
import FlipperAppStateOwner = com.kitakkun.backintime.tooling.flipper.FlipperAppStateOwner;
import TabState = com.kitakkun.backintime.tooling.flipper.TabState;
import BackInTimeDebuggerEvent = com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent;
import KtList = kotlin.collections.KtList;

export function InstanceListPage() {
  const instanceListState = selectInstanceListState()
  const [backInTimeTargetInstanceId, setBackInTimeTargetInstanceId] = useState<string | null>(null)

  return <>
    {
      backInTimeTargetInstanceId ?
        <BackInTimeModalPage
          instanceId={backInTimeTargetInstanceId}
          onDismissRequest={() => setBackInTimeTargetInstanceId(null)}
        /> : <></>
    }
    <InstanceListView
      state={instanceListState}
      onSelectProperty={(instanceUUID, propertySignature) => {
        FlipperAppStateOwner.updateTabState(new TabState.InstanceTabState(instanceUUID, propertySignature))
      }}
      onClickRefresh={() => {
        const uuids = instanceListState.instances.asJsReadonlyArrayView().map((info) => info.uuid);
        if (uuids.length == 0) return;
        FlipperAppStateOwner.postDebuggerEvent(
          new BackInTimeDebuggerEvent.CheckInstanceAlive(KtList.fromJsArray(instanceListState.instances.asJsReadonlyArrayView().map((item) => item.uuid)))
        )
      }}
      onChangeNonDebuggablePropertyVisible={(visible) => {
        // TODO:
      }}
      onClickHistory={(instanceUUID) => {
        setBackInTimeTargetInstanceId(instanceUUID)
      }}
    />
  </>
}

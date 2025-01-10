import {createState, PluginClient, usePlugin} from "flipper-plugin";
import React, {useEffect, useMemo} from "react";
import {com} from "backintime-flipper-lib";
import FlipperAppState = com.kitakkun.backintime.tooling.flipper.FlipperAppState;
import useStateFlow = com.kitakkun.backintime.tooling.flipper.useStateFlow;
import BackInTimeComponent from "./BackInTimeComponent";
import {LocalStateOwner} from "./context/StateOwnerContext";
import {LocalAppState} from "./context/LocalAppState";
import FlipperAppStateOwnerImpl = com.kitakkun.backintime.tooling.flipper.FlipperAppStateOwnerImpl;
import IncomingEvents = com.kitakkun.backintime.tooling.flipper.IncomingEvents;
import OutgoingEvents = com.kitakkun.backintime.tooling.flipper.OutgoingEvents;

// @ts-ignore
export function plugin(client: PluginClient<IncomingEvents, OutgoingEvents>) {
  // do almost all the stuff on the Component side
  return {
    client: client,
    // persistToLocalStorage can only be used inside plugin function
    showNonDebuggableProperty: createState(true, {persist: "showNonDebuggableProperty", persistToLocalStorage: true})
  }
}

export function Component() {
  const pluginInstance = usePlugin(plugin)
  const client = pluginInstance.client
  const showNonDebuggableProperty = pluginInstance.showNonDebuggableProperty
  const appStateOwner = useMemo(() => new FlipperAppStateOwnerImpl(client, showNonDebuggableProperty), [client, showNonDebuggableProperty])

  // @ts-ignore
  const appState: FlipperAppState = useStateFlow(appStateOwner.stateFlow)
  return (
    <LocalStateOwner.Provider value={appStateOwner}>
      <LocalAppState.Provider value={appState}>
        <BackInTimeComponent/>
      </LocalAppState.Provider>
    </LocalStateOwner.Provider>
  )
}

// Read more: https://fbflipper.com/docs/tutorial/js-custom#creating-a-first-plugin
// API: https://fbflipper.com/docs/extending/flipper-plugin#pluginclient
import {PluginClient} from "flipper-plugin";
import {IncomingEvents} from "./events/FlipperIncomingEvents";
import {OutgoingEvents} from "./events/FlipperOutgoingEvents";
import * as flipperLib from "backintime-flipper-lib";
import FlipperAppStateOwner = flipperLib.com.kitakkun.backintime.tooling.flipper.FlipperAppStateOwner;

export default (client: PluginClient<IncomingEvents, OutgoingEvents>) => {
  const stateOwner = FlipperAppStateOwner

  client.onMessage("appEvent", (appEvent) => {
    stateOwner.processEvent(appEvent.payload)
  })
  
  // TODO: send event to the app
}

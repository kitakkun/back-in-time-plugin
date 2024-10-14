import BackInTimePlugin from "./BackInTimePlugin";
import {PluginClient} from "flipper-plugin";
import {IncomingEvents} from "./events/FlipperIncomingEvents";
import {OutgoingEvents} from "./events/FlipperOutgoingEvents";
import React from "react";
import BackInTimeComponent from "./BackInTimeComponent";
import {BackInTimeStoreProvider} from "./BackInTimeStoreProvider";

export function plugin(client: PluginClient<IncomingEvents, OutgoingEvents>) {
  return BackInTimePlugin(client);
}

export function Component() {
  return (
    <BackInTimeStoreProvider>
      <BackInTimeComponent/>
    </BackInTimeStoreProvider>
  );
}

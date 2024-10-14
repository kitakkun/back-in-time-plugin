import {io} from "backintime-websocket-event";
import BackInTimeDebuggerEvent = io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent;
import BackInTimeDebugServiceEvent = io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent;

export type OutgoingEvents = {
  forceSetPropertyValue(event: BackInTimeDebuggerEvent.ForceSetPropertyValue): Promise<any>;
  refreshInstanceAliveStatus(event: BackInTimeDebuggerEvent.CheckInstanceAlive): Promise<BackInTimeDebugServiceEvent.CheckInstanceAliveResult>;
}

// FYI https://timmousk.com/blog/typescript-instanceof-interface/
export const isForceSetPropertyValue = (event: BackInTimeDebuggerEvent): event is BackInTimeDebuggerEvent.ForceSetPropertyValue => {
  return event.constructor.name == "ForceSetPropertyValue"
}

export const isCheckInstanceAlive = (event: BackInTimeDebuggerEvent): event is BackInTimeDebuggerEvent.CheckInstanceAlive => {
  return event.constructor.name == "CheckInstanceAlive"
}

import {com} from "backintime-websocket-event";
import BackInTimeDebuggerEvent = com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent;
import BackInTimeDebugServiceEvent = com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent;

interface DebuggerAction {
  payload: string;
}

export type OutgoingEvents = {
  debuggerEvent(action: DebuggerAction): Promise<any>;
}

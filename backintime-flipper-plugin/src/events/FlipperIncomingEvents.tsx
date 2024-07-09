import {io} from "backintime-websocket-event";
import BackInTimeDebugServiceEvent = io.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent;

export type IncomingEvents = {
  register: BackInTimeDebugServiceEvent.RegisterInstance;
  notifyValueChange: BackInTimeDebugServiceEvent.NotifyValueChange;
  notifyMethodCall: BackInTimeDebugServiceEvent.NotifyMethodCall;
  registerRelationship: BackInTimeDebugServiceEvent.RegisterRelationship;
  checkInstanceAliveResult: BackInTimeDebugServiceEvent.CheckInstanceAliveResult;
};

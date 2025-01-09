interface DebuggerAction {
  payload: string;
}

export type OutgoingEvents = {
  debuggerEvent(action: DebuggerAction): Promise<any>;
}

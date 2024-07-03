export type OutgoingEvents = {
  forceSetPropertyValue(event: ForceSetPropertyValue): Promise<any>;
  refreshInstanceAliveStatus(event: CheckInstanceAlive): Promise<CheckInstanceAliveResponse>;
}

export interface OutgoingEvent {
}

export interface ForceSetPropertyValue extends OutgoingEvent {
  instanceUUID: string;
  propertyName: string;
  value: string;
  valueType: string;
}

export interface CheckInstanceAlive extends OutgoingEvent {
  instanceUUIDs: string[];
}

export type CheckInstanceAliveResponse = {
  isAlive: Map<string, boolean>;
}

// FYI https://timmousk.com/blog/typescript-instanceof-interface/
export const isForceSetPropertyValue = (event: OutgoingEvent): event is ForceSetPropertyValue => {
  return 'value' in event;
}

export const isCheckInstanceAlive = (event: OutgoingEvent): event is CheckInstanceAlive => {
  return 'instanceUUIDs' in event;
}
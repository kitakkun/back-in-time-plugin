export interface MethodCallInfo {
  callUUID: string;
  instanceUUID: string;
  methodName: string;
  calledAt: number;
  valueChanges: ValueChangeInfo[];
}

export interface ValueChangeInfo {
  propertyName: string;
  value: string;
}
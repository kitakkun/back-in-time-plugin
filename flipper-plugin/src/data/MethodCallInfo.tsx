export interface MethodCallInfo {
  callUUID: string;
  instanceUUID: string;
  methodSignature: string;
  calledAt: number;
  valueChanges: ValueChangeInfo[];
}

export interface ValueChangeInfo {
  propertySignature: string;
  value: string;
}

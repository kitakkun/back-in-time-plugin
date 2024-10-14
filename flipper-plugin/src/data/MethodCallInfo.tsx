export interface MethodCallInfo {
  ownerClassFqName: string;
  callUUID: string;
  instanceUUID: string;
  methodName: string;
  calledAt: number;
  valueChanges: ValueChangeInfo[];
}

export interface ValueChangeInfo {
  ownerClassFqName: string;
  propertyName: string;
  value: string;
}

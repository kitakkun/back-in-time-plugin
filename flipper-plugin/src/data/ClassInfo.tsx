export interface ClassInfo {
  classSignature: string;
  superClassName: string;
  properties: PropertyInfo[];
}

export interface PropertyInfo {
  signature: string;
  type: string;
  valueType: string;
  debuggable: boolean;
  isDebuggableStateHolder: boolean;
}
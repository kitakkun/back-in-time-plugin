export interface ClassInfo {
  name: string;
  superClassName: string;
  properties: PropertyInfo[];
}

export interface PropertyInfo {
  name: string;
  type: string;
  valueType: string;
  debuggable: boolean;
  isDebuggableStateHolder: boolean;
}
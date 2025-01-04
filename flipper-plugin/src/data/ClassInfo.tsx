import {com} from "backintime-websocket-event";
import PropertyInfo = com.kitakkun.backintime.core.websocket.event.model.PropertyInfo;

export interface ClassInfo {
  classSignature: string;
  superClassName: string;
  properties: PropertyInfo[];
}

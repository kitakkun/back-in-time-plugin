import {com} from "backintime-tooling-model";
import ValueChangeInfo = com.kitakkun.backintime.tooling.model.ValueChangeInfo;

export interface HistoryInfo {
  title: string;
  subtitle: string;
  timestamp: number;
  description: string;
}

export interface RegisterHistoryInfo extends HistoryInfo {
  title: "register";
}

export interface MethodCallHistoryInfo extends HistoryInfo {
  title: "methodCall";
  valueChanges: ValueChangeInfo[];
}
import React from "react";
import {StepProps, Steps} from "antd";
import {BackInTimeState} from "./BackInTimeSelector";

export interface BackInTimeViewProps {
  state: BackInTimeState;
  onSelectHistory: (index: number) => void;
}

export function BackInTimeView({state, onSelectHistory}: BackInTimeViewProps) {
  const items: StepProps[] = state.histories.map((history, index) => ({
    title: history.title,
    subTitle: history.subtitle,
    description: history.description,
    status: "finish"
  }));

  return (
    <Steps
      progressDot
      direction={"vertical"}
      size={"small"}
      items={items}
      onChange={onSelectHistory}
    />
  );
}

import React from "react";
import {StepProps, Steps} from "antd";
import {com} from "backintime-flipper-lib";
import BackInTimeState = com.kitakkun.backintime.tooling.model.ui.BackInTimeState;

export interface BackInTimeViewProps {
  state: BackInTimeState;
  onSelectHistory: (index: number) => void;
}

export function BackInTimeView({state, onSelectHistory}: BackInTimeViewProps) {
  const items: StepProps[] = state.histories.asJsReadonlyArrayView().map((history, index) => ({
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

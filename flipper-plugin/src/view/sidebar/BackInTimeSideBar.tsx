import {DetailSidebar} from "flipper-plugin";
import React from "react";
import {PropertyInspectorPage} from "./property_inspector/PropertyInspectorPage";
import {RawLogInspectorPage} from "./raw_log_inspector/RawLogInspectorPage";
import {com} from "backintime-flipper-lib";
import selectInstanceTabState = com.kitakkun.backintime.tooling.flipper.selector.selectInstanceTabState;
import selectLogTabState = com.kitakkun.backintime.tooling.flipper.selector.selectLogTabState;
import {useAppState} from "../../context/LocalAppState";
import FlipperTab = com.kitakkun.backintime.tooling.flipper.FlipperTab;

export function BackInTimeSideBar() {
  const appState = useAppState()
  const instanceTabState = selectInstanceTabState(appState)
  const logTabState = selectLogTabState(appState)

  return (
    <DetailSidebar>
      {
        appState.activeTabIndex == FlipperTab.Instances &&
        instanceTabState &&
        (
          (instanceTabState.selectedInstanceId && instanceTabState.selectedPropertyId) ?
            <PropertyInspectorPage
              instanceId={instanceTabState.selectedInstanceId}
              propertySignature={instanceTabState.selectedPropertyId}
            /> : <>No property selected.</>
        )
      }
      {
        appState.activeTabIndex == FlipperTab.Logs &&
        (
          logTabState?.selectedEvent ? <RawLogInspectorPage/> : <>No event selected.</>
        )
      }
    </DetailSidebar>
  );
}
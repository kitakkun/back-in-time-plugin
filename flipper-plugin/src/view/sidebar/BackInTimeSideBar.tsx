import {DetailSidebar} from "flipper-plugin";
import React from "react";
import {PropertyInspectorPage} from "./property_inspector/PropertyInspectorPage";
import {RawLogInspectorPage} from "./raw_log_inspector/RawLogInspectorPage";
import {com} from "backintime-flipper-lib";
import selectInstanceTabState = com.kitakkun.backintime.tooling.flipper.selector.selectInstanceTabState;
import selectLogTabState = com.kitakkun.backintime.tooling.flipper.selector.selectLogTabState;

export function BackInTimeSideBar() {
  const instanceTabState = selectInstanceTabState()
  const logTabState = selectLogTabState()
  
  return (
    <DetailSidebar>
      {instanceTabState &&
          instanceTabState.selectedInstanceId &&
          instanceTabState.selectedPropertyId &&
          <PropertyInspectorPage
              instanceId={instanceTabState.selectedInstanceId}
              propertySignature={instanceTabState.selectedPropertyId}
          />
      }
      {logTabState && <RawLogInspectorPage />}
    </DetailSidebar>
  );
}
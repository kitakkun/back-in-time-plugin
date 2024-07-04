import {DetailSidebar} from "flipper-plugin";
import React from "react";
import {useSelector} from "react-redux";
import {PropertyInspectorPage} from "./property_inspector/PropertyInspectorPage";
import {RawLogInspectorPage} from "./raw_log_inspector/RawLogInspectorPage";
import {appStateSelector} from "../../reducer/appReducer";

export function BackInTimeSideBar() {
  const appState = useSelector(appStateSelector);

  return (
    <DetailSidebar>
      {appState.activeTabIndex == '1' && <PropertyInspectorPage/>}
      {appState.activeTabIndex == '2' && <RawLogInspectorPage/>}
    </DetailSidebar>
  );
}
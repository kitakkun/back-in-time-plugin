// Read more: https://fbflipper.com/docs/tutorial/js-custom#building-a-user-interface-for-the-plugin
// API: https://fbflipper.com/docs/extending/flipper-plugin#react-hooks
import {Layout} from "flipper-plugin";
import React from "react";
import {BackInTimeSideBar} from "./view/sidebar/BackInTimeSideBar";
import {TabbedContent} from "./view/component/TabbedContent";
import {com} from "backintime-flipper-lib";
import useStateFlow = com.kitakkun.backintime.tooling.flipper.useStateFlow;
import FlipperAppStateOwner = com.kitakkun.backintime.tooling.flipper.FlipperAppStateOwner;
import FlipperAppState = com.kitakkun.backintime.tooling.flipper.FlipperAppState;
import FlipperTab = com.kitakkun.backintime.tooling.flipper.FlipperTab;

export default () => {
  const state: FlipperAppState = useStateFlow(FlipperAppStateOwner.stateFlow)

  return (
    <>
      <Layout.Container grow={true}>
        <TabbedContent
          activeKey={state.activeTabIndex}
          onChange={
            (key) => FlipperAppStateOwner.updateTab(FlipperTab.values()[Number(key)])
          }
        />
      </Layout.Container>
      <BackInTimeSideBar/>
    </>
  );
}

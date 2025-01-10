// Read more: https://fbflipper.com/docs/tutorial/js-custom#building-a-user-interface-for-the-plugin
// API: https://fbflipper.com/docs/extending/flipper-plugin#react-hooks
import {Layout} from "flipper-plugin";
import React from "react";
import {BackInTimeSideBar} from "./view/sidebar/BackInTimeSideBar";
import {TabbedContent} from "./view/component/TabbedContent";
import {com} from "backintime-flipper-lib";
import FlipperTab = com.kitakkun.backintime.tooling.flipper.FlipperTab;
import {useAppState} from "./context/LocalAppState";
import {useStateOwner} from "./context/StateOwnerContext";

export default () => {
  const state = useAppState()
  const owner = useStateOwner()

  return (
    <>
      <Layout.Container grow={true}>
        <TabbedContent
          activeKey={state.activeTabIndex}
          onChange={
            (key) => owner.updateTab(FlipperTab.values()[Number(key)])
          }
        />
      </Layout.Container>
      <BackInTimeSideBar/>
    </>
  );
}

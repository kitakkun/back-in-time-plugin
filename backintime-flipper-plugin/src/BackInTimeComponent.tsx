// Read more: https://fbflipper.com/docs/tutorial/js-custom#building-a-user-interface-for-the-plugin
// API: https://fbflipper.com/docs/extending/flipper-plugin#react-hooks
import {Layout} from "flipper-plugin";
import React from "react";
import {BackInTimeSideBar} from "./view/sidebar/BackInTimeSideBar";
import {TabbedContent} from "./view/component/TabbedContent";
import {useDispatch, useSelector} from "react-redux";
import {appActions, selectActiveTabIndex} from "./reducer/appReducer";

export default () => {
  const activeKey = useSelector(selectActiveTabIndex);
  const dispatch = useDispatch();

  return (
    <>
      <Layout.Container grow={true}>
        <TabbedContent
          activeKey={activeKey}
          onChange={(key) => dispatch(appActions.updateActiveTabIndex(key))}
        />
      </Layout.Container>
      <BackInTimeSideBar/>
    </>
  );
}

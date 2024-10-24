import {useDispatch, useSelector} from "react-redux";
import React from "react";
import {InstanceListView} from "./InstanceListView";
import {persistentStateActions} from "../../../reducer/PersistentStateReducer";
import {selectInstanceList} from "./InstanceListSelector";
import {appActions} from "../../../reducer/appReducer";
import {propertyInspectorActions} from "../../sidebar/property_inspector/propertyInspectorReducer";
import {backInTimeActions} from "../backintime/BackInTimeReducer";
import {BackInTimeModalPage} from "../backintime/BackInTimeModalPage";
import {io} from "backintime-websocket-event";
import createCheckInstanceAliveEvent = io.github.kitakkun.backintime.core.websocket.event.createCheckInstanceAliveEvent;

export function InstanceListPage() {
  const state = useSelector(selectInstanceList);
  const dispatch = useDispatch();

  return <>
    <BackInTimeModalPage/>
    <InstanceListView
      state={state}
      onSelectProperty={(instanceUUID, propertyName) => {
        dispatch(propertyInspectorActions.openPropertyInspector({
          instanceUUID: instanceUUID,
          propertyName: propertyName,
        }))
      }}
      onClickRefresh={() => {
        const uuids = state.instances.map((info) => info.uuid);
        if (uuids.length == 0) return;
        dispatch(appActions.refreshInstanceAliveStatuses(createCheckInstanceAliveEvent(uuids)));
      }}
      onChangeNonDebuggablePropertyVisible={(visible) => {
        dispatch(persistentStateActions().updateNonDebuggablePropertyVisibility(visible));
      }}
      onClickHistory={(instanceUUID) => {
        dispatch(backInTimeActions.open({instanceUUID: instanceUUID}));
      }}
    />
  </>
}

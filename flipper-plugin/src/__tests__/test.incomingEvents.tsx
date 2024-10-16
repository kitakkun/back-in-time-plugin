import {TestUtils} from 'flipper-plugin';
import * as Plugin from '..';
import {InstanceInfo} from "../data/InstanceInfo";
import {AppState} from "../reducer/appReducer";
import {ClassInfo} from "../data/ClassInfo";
import {MethodCallInfo} from "../data/MethodCallInfo";
import {io} from "backintime-websocket-event";
import NotifyMethodCall = io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent.NotifyMethodCall;
import NotifyValueChange = io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent.NotifyValueChange;

test(`register event`, () => {
  const {instance, sendEvent} = TestUtils.startPlugin(Plugin);
  const store = instance.store;

  sendEvent("register", {
    instanceUUID: "7fd43b42-f951-4307-a997-85f6074c17c9",
    className: "com.example.DummyViewModel",
    // @ts-ignore
    properties: [
      {
        name: "hoge",
        debuggable: true,
        isDebuggableStateHolder: false,
        propertyType: "java.lang.String",
        valueType: "java.lang.String"
      },
      {
        name: "fuga",
        debuggable: false,
        isDebuggableStateHolder: false,
        propertyType: "java.lang.String",
        valueType: "java.lang.String"
      }
    ],
    registeredAt: 1619813420,
  });

  const state = store.getState().app as AppState;

  expect(state.instanceInfoList[0]).toEqual({
    uuid: "7fd43b42-f951-4307-a997-85f6074c17c9",
    className: "com.example.DummyViewModel",
    alive: true,
    registeredAt: 1619813420,
  } as InstanceInfo);

  expect(state.classInfoList[0]).toEqual({
    name: "com.example.DummyViewModel",
    properties: [
      {
        name: "hoge",
        debuggable: true,
        isDebuggableStateHolder: false,
        type: "java.lang.String",
        valueType: "java.lang.String"
      },
      {
        name: "fuga",
        debuggable: false,
        isDebuggableStateHolder: false,
        type: "java.lang.String",
        valueType: "java.lang.String"
      }
    ]
  } as ClassInfo);
});

test(`notifyMethodCall event`, () => {
  const {instance, sendEvent} = TestUtils.startPlugin(Plugin);
  const store = instance.store;

  sendEvent("notifyMethodCall", {
    methodName: "hoge",
    ownerClassFqName: "com.example.MyClass",
    instanceUUID: "7fd43b42-f951-4307-a997-85f6074c17c9",
    methodCallUUID: "jf245181-8d9f-4d9e-9a7b-1a7f4b6f0b3e",
    calledAt: 1619813420,
  } as NotifyMethodCall);

  const state = store.getState().app as AppState;

  expect(state.methodCallInfoList[0]).toEqual({
    instanceUUID: "7fd43b42-f951-4307-a997-85f6074c17c9",
    ownerClassFqName: "com.example.MyClass",
    methodName: "hoge",
    callUUID: "jf245181-8d9f-4d9e-9a7b-1a7f4b6f0b3e",
    calledAt: 1619813420,
    valueChanges: [],
  } as MethodCallInfo);
});

test(`notifyValueChange event`, () => {
  const {instance, sendEvent} = TestUtils.startPlugin(Plugin);
  const store = instance.store;

  const instanceUUID = "7fd43b42-f951-4307-a997-85f6074c17c9";
  const methodCallUUID = "jf245181-8d9f-4d9e-9a7b-1a7f4b6f0b3e";
  const calledAt = 1619813420;

  sendEvent("notifyMethodCall", {
    methodName: "hoge",
    ownerClassFqName: "com.example.MyClass",
    instanceUUID: instanceUUID,
    methodCallUUID: methodCallUUID,
    calledAt: calledAt,
  } as NotifyMethodCall);

  sendEvent("notifyValueChange", {
    instanceUUID: instanceUUID,
    ownerClassFqName: "com.example.MyClass",
    propertyName: "hoge",
    value: "fuga",
    methodCallUUID: methodCallUUID,
  } as NotifyValueChange);

  expect(store.getState().app.methodCallInfoList[0]).toEqual({
    ownerClassFqName: "com.example.MyClass",
    instanceUUID: instanceUUID,
    methodName: "hoge",
    callUUID: methodCallUUID,
    calledAt: calledAt,
    valueChanges: [
      {
        ownerClassFqName: "com.example.MyClass",
        propertyName: "hoge",
        value: "fuga",
      }
    ],
  } as MethodCallInfo);
});

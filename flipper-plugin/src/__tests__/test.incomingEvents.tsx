import {TestUtils} from 'flipper-plugin';
import * as Plugin from '..';
import {InstanceInfo} from "../data/InstanceInfo";
import {AppState} from "../reducer/appReducer";
import {ClassInfo} from "../data/ClassInfo";
import {MethodCallInfo} from "../data/MethodCallInfo";
import {com} from "backintime-websocket-event";
import NotifyMethodCall = com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent.NotifyMethodCall;
import NotifyValueChange = com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent.NotifyValueChange;
import RegisterInstance = com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent.RegisterInstance;

test(`register event`, () => {
  const {instance, sendEvent} = TestUtils.startPlugin(Plugin);
  const store = instance.store;

  sendEvent("register", {
    instanceUUID: "7fd43b42-f951-4307-a997-85f6074c17c9",
    classSignature: "com/example/DummyViewModel",
    superClassSignature: "unknown",
    // @ts-ignore
    properties: [
      {
        signature: "com/example/DummyViewModel.hoge",
        debuggable: true,
        isDebuggableStateHolder: false,
        propertyType: "kotlin/String",
        valueType: "kotlin/String"
      },
      {
        signature: "com/example/DummyViewModel.fuga",
        debuggable: false,
        isDebuggableStateHolder: false,
        propertyType: "kotlin/String",
        valueType: "kotlin/String"
      }
    ],
    registeredAt: 1619813420,
  });

  const state = store.getState().app as AppState;

  expect(state.instanceInfoList[0]).toEqual({
    uuid: "7fd43b42-f951-4307-a997-85f6074c17c9",
    classSignature: "com/example/DummyViewModel",
    alive: true,
    registeredAt: 1619813420,
  } as InstanceInfo);

  expect(state.classInfoList[0]).toEqual({
    classSignature: "com/example/DummyViewModel",
    superClassName: "unknown",
    properties: [
      {
        signature: "com/example/DummyViewModel.hoge",
        debuggable: true,
        isDebuggableStateHolder: false,
        propertyType: "kotlin/String",
        valueType: "kotlin/String"
      },
      {
        signature: "com/example/DummyViewModel.fuga",
        debuggable: false,
        isDebuggableStateHolder: false,
        propertyType: "kotlin/String",
        valueType: "kotlin/String"
      }
    ]
  } as ClassInfo);
});

test(`notifyMethodCall event`, () => {
  const {instance, sendEvent} = TestUtils.startPlugin(Plugin);
  const store = instance.store;

  sendEvent("notifyMethodCall", {
    methodSignature: "com/example/MyClass.hoge():kotlin/Unit",
    instanceUUID: "7fd43b42-f951-4307-a997-85f6074c17c9",
    methodCallUUID: "jf245181-8d9f-4d9e-9a7b-1a7f4b6f0b3e",
    calledAt: 1619813420,
  } as NotifyMethodCall);

  const state = store.getState().app as AppState;

  expect(state.methodCallInfoList[0]).toEqual({
    instanceUUID: "7fd43b42-f951-4307-a997-85f6074c17c9",
    methodSignature: "com/example/MyClass.hoge():kotlin/Unit",
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
    methodSignature: "com/example/MyClass.hoge():kotlin/Unit",
    instanceUUID: instanceUUID,
    methodCallUUID: methodCallUUID,
    calledAt: calledAt,
  } as NotifyMethodCall);

  sendEvent("notifyValueChange", {
    instanceUUID: instanceUUID,
    propertySignature: "com/example/MyClass.hoge",
    value: "fuga",
    methodCallUUID: methodCallUUID,
  } as NotifyValueChange);

  expect(store.getState().app.methodCallInfoList[0]).toEqual({
    instanceUUID: instanceUUID,
    methodSignature: "com/example/MyClass.hoge():kotlin/Unit",
    callUUID: methodCallUUID,
    calledAt: calledAt,
    valueChanges: [
      {
        propertySignature: "com/example/MyClass.hoge",
        value: "fuga",
      }
    ],
  } as MethodCallInfo);
});

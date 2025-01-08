import {TestUtils} from 'flipper-plugin';
import * as Plugin from '..';
import {InstanceInfo} from "../data/InstanceInfo";
import {AppState} from "../reducer/appReducer";

test(`register event`, () => {
  const {instance, sendEvent} = TestUtils.startPlugin(Plugin);
  const store = instance.store;

  sendEvent("appEvent", {
    payload: `
    {
      "type": "com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent.RegisterInstance",
      "instanceUUID": "7fd43b42-f951-4307-a997-85f6074c17c9",
      "classSignature": "com/example/DummyViewModel",
      "superClassSignature": "unknown",
      "properties": [
          {
              "signature": "com/example/DummyViewModel.hoge",
              "debuggable": true,
              "isDebuggableStateHolder": false,
              "propertyType": "kotlin/String",
              "valueType": "kotlin/String"
          },
          {
              "signature": "com/example/DummyViewModel.fuga",
              "debuggable": false,
              "isDebuggableStateHolder": false,
              "propertyType": "kotlin/String",
              "valueType": "kotlin/String"
          }
      ],
      "registeredAt": 1619813420
    }
    `
  });

  const state = store.getState().app as AppState;

  expect(state.instanceInfoList[0]).toEqual({
    uuid: "7fd43b42-f951-4307-a997-85f6074c17c9",
    classSignature: "com/example/DummyViewModel",
    alive: true,
    registeredAt: 1619813420,
  } as InstanceInfo);

  expect(state.classInfoList[0].classSignature).toEqual("com/example/DummyViewModel")
  expect(state.classInfoList[0].superClassSignature).toEqual("unknown")
  const firstProp = state.classInfoList[0].properties.asJsReadonlyArrayView()[0]
  const secondProp = state.classInfoList[0].properties.asJsReadonlyArrayView()[1]
  expect(firstProp.signature).toEqual("com/example/DummyViewModel.hoge")
  expect(firstProp.debuggable).toEqual(true)
  expect(firstProp.isDebuggableStateHolder).toEqual(false)
  expect(firstProp.propertyType).toEqual("kotlin/String")
  expect(firstProp.valueType).toEqual("kotlin/String")
  expect(secondProp.signature).toEqual("com/example/DummyViewModel.fuga")
  expect(secondProp.debuggable).toEqual(false)
  expect(secondProp.isDebuggableStateHolder).toEqual(false)
  expect(secondProp.propertyType).toEqual("kotlin/String")
  expect(secondProp.valueType).toEqual("kotlin/String")
});

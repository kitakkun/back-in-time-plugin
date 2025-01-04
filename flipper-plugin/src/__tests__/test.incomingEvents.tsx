import {TestUtils} from 'flipper-plugin';
import * as Plugin from '..';
import {InstanceInfo} from "../data/InstanceInfo";
import {AppState} from "../reducer/appReducer";
import {ClassInfo} from "../data/ClassInfo";

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

  expect(state.classInfoList[0]).toEqual({
    classSignature: "com/example/DummyViewModel",
    superClassSignature: "unknown",
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

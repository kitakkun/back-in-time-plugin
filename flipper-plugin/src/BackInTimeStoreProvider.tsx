import {usePlugin} from "flipper-plugin";
import {plugin} from "./index";
import React from "react";
import {Provider} from "react-redux";

type BackInTimeStoreProviderProps = {
  children: React.ReactNode;
}

export function BackInTimeStoreProvider({children}: BackInTimeStoreProviderProps) {
  const pluginInstance = usePlugin(plugin);
  const store = pluginInstance.store;

  return (
    <Provider store={store}>
      {children}
    </Provider>
  )
}
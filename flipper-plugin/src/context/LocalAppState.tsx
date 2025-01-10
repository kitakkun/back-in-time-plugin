import {createContext, useContext} from "react";
import {com} from "backintime-flipper-lib";
import FlipperAppState = com.kitakkun.backintime.tooling.flipper.FlipperAppState;

export const LocalAppState = createContext<FlipperAppState>(FlipperAppState.Companion.Default)

export function useAppState(): FlipperAppState {
  return useContext(LocalAppState)
}

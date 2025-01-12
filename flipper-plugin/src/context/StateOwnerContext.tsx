import {createContext, useContext} from "react";
import {com} from "backintime-flipper-lib";
import FlipperAppStateOwner = com.kitakkun.backintime.tooling.flipper.FlipperAppStateOwner;

export const LocalStateOwner = createContext<FlipperAppStateOwner | null>(null)

export function useStateOwner(): FlipperAppStateOwner {
  return useContext(LocalStateOwner)!
}

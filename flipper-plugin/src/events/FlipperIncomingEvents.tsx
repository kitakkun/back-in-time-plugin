interface AppEvent {
  payload: string;
}

export type IncomingEvents = {
  appEvent: AppEvent;
};

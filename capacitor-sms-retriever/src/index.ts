import { registerPlugin } from '@capacitor/core';

export interface SmsRetrieverPlugin {
  start(): Promise<{ started: boolean } | { message: string }>;
  stop(): Promise<void>;
  addListener(
    eventName: 'smsReceived',
    listenerFunc: (data: { message: string }) => void
  ): Promise<any>;
  addListener(
    eventName: 'smsTimeout',
    listenerFunc: () => void
  ): Promise<any>;
  removeAllListeners(): Promise<void>;
}

export const SmsRetriever = registerPlugin<SmsRetrieverPlugin>('SmsRetriever');
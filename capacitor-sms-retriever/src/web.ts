import { WebPlugin } from '@capacitor/core';

import type { CapacitorSmsRetrieverPlugin } from './definitions';

export class CapacitorSmsRetrieverWeb extends WebPlugin implements CapacitorSmsRetrieverPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}

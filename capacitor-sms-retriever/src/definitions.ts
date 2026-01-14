export interface CapacitorSmsRetrieverPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}

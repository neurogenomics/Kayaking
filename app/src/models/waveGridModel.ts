type WaveModel = {
  height: number;
  direction: number;
};

export type WaveGridModel = {
  grid: WaveModel[][];
  latIndex: number[];
  lonIndex: number[];
};

import { Vector } from './vectorModel';

export type ResolutionModel = {
  latRes: number;
  lonRes: number;
};

export type GridModel = {
  grid: Vector[][];
  latIndex: number[];
  lonIndex: number[];
};

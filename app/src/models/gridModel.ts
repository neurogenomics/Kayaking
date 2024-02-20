import { Vector } from './vectorModel';

export type ResolutionModel = {
  latRes: number;
  lonRes: number;
};

export enum GridType {
  WIND = 'Wind',
  TIDE = 'Tide',
}

export type GridModel = {
  grid: Vector[][];
  latIndex: number[];
  lonIndex: number[];
};

import { Vector } from './vectorModel';

export type ResolutionModel = {
  latRes: number;
  lonRes: number;
};

export enum GridType {
  WIND,
  TIDE,
}

export type GridModel = {
  grid: Vector[][];
  latIndex: number[];
  lonIndex: number[];
};

export type CoordRotation = {
  coord: {
    longitude: number;
    latitude: number;
  };
  direction: number;
};

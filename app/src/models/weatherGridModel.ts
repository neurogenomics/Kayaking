import { Vector } from './vectorModel';

export type ResolutionModel = {
  latRes: number;
  lonRes: number;
};

export enum WeatherGridType {
  WIND = 'Wind',
  TIDE = 'Tide',
}

export type WeatherGridModel = {
  grid: Vector[][];
  latIndex: number[];
  lonIndex: number[];
};

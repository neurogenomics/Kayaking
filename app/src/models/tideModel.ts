import { LocationModel } from './locationModel';

export type TideInfo = {
  events: TideEvent[];
  source: TideStation;
};

export type TideStation = {
  id: string;
  name: string;
  location: LocationModel;
};

export type TideEvent = {
  isHighTide: boolean;
  datetime: Date;
  height: number | null;
};

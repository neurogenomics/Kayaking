import { LocationModel } from './locationModel';

export type Route = {
  startTime: Date;
  endTime: Date;
  difficulty: number;
  length: number;
  locations: LocationModel[];
};

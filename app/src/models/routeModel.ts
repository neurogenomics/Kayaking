import { LocationModel } from './locationModel';

export type RouteModel = {
  name: string;
  length: number;
  locations: LocationModel[];
};

export const getDistance = (route: RouteModel): string => {
  return (route.length / 1000).toFixed(2).toString();
};

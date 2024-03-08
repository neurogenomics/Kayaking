import { LocationModel } from './src/models/locationModel';
import { ResolutionModel } from './src/models/weatherGridModel';

// TODO: get constants from server
export const gridStart: LocationModel = {
  latitude: 50.448253,
  longitude: -1.770676,
};
export const gridEnd: LocationModel = {
  latitude: 50.934747,
  longitude: -0.842729,
};
// TODO: add feature so resolution increases as user zooms
export const gridResolution: ResolutionModel = {
  latRes: 0.05,
  lonRes: 0.05,
};

export const isleOfWight = {
  longitude: -1.33,
  latitude: 50.67,
  longitudeDelta: 0.56,
  latitudeDelta: 0.22,
};

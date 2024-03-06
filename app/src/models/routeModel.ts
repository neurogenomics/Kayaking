import {
  LocationModel,
  calculateDistanceBetweenLocations,
} from './locationModel';

export type RouteModel = {
  name: string;
  length: number;
  locations: LocationModel[];
  difficulty: number;
  endTime: Date;
  startTime: Date;
  checkpoints: number[];
};
export const getDistance = (route: RouteModel): string => {
  return (route.length / 1000).toFixed(2).toString();
};

export const getMapDisplayRegion = (route: RouteModel) => {
  const latitudes = route.locations.map((location) => location.latitude);
  const longitudes = route.locations.map((location) => location.longitude);
  const minLat = Math.min(...latitudes);
  const maxLat = Math.max(...latitudes);
  const minLng = Math.min(...longitudes);
  const maxLng = Math.max(...longitudes);

  // Calculate deltas for latitude and longitude
  const deltaLat = (maxLat - minLat) * 1.1; // Add some padding
  const deltaLng = (maxLng - minLng) * 1.1; // Add some padding

  // Calculate center of the region
  const centerLat = (minLat + maxLat) / 2;
  const centerLng = (minLng + maxLng) / 2;

  const region = {
    latitude: centerLat,
    longitude: centerLng,
    latitudeDelta: deltaLat,
    longitudeDelta: deltaLng,
  };
  return region;
};

export const getRouteSpeeds = (route: RouteModel) => {
  const speeds: number[] = [];
  for (let i = 0; i < route.locations.length - 1; i++) {
    const distance = calculateDistanceBetweenLocations(
      route.locations[i],
      route.locations[i + 1],
    );
    const time = route.checkpoints[i + 1] - route.checkpoints[i];
    speeds.push(distance / time);
  }
  return speeds;
};

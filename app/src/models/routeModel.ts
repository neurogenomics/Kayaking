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

export function getDifficultyLabel(difficulty: number): string {
  if (difficulty <= 4) {
    return 'Easy';
  } else if (difficulty <= 7) {
    return 'Medium';
  } else {
    return 'Hard';
  }
}

export const isCircular = (route: RouteModel) => {
  const start = route.locations[0];
  const end = route.locations.slice(-1)[0];
  return start.latitude === end.latitude && start.longitude === end.longitude;
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
    if (time === 0) {
      speeds.push(i === 0 ? 0 : speeds[i - 1]);
    } else speeds.push(distance / time);
  }
  return speeds;
};

export type LocationModel = {
  latitude: number;
  longitude: number;
};

export const locationToString = (location: LocationModel): string => {
  return `Latitude: ${location.latitude}, Longitude: ${location.longitude}`;
};


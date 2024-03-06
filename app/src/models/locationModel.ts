export type LocationModel = {
  latitude: number;
  longitude: number;
};

export function calculateDistanceBetweenLocations(
  location1: LocationModel,
  location2: LocationModel,
): number {
  const earthRadiusKm = 6371000; // Radius of the Earth in kilometers

  const lat1 = toRadians(location1.latitude);
  const lon1 = toRadians(location1.longitude);
  const lat2 = toRadians(location2.latitude);
  const lon2 = toRadians(location2.longitude);

  const latDiff = lat2 - lat1;
  const lonDiff = lon2 - lon1;

  const a =
    Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
    Math.cos(lat1) *
      Math.cos(lat2) *
      Math.sin(lonDiff / 2) *
      Math.sin(lonDiff / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  const distance = earthRadiusKm * c;

  return distance;
}

function toRadians(degrees: number): number {
  return degrees * (Math.PI / 180);
}

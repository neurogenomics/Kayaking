import { LocationModel } from '../models/locationModel';
import { Vector } from '../models/vectorModel';
import { getData } from './utils';
import { format } from 'date-fns';

export const getWindDirection = async (
  location: LocationModel,
  date?: Date,
): Promise<Vector> => {
  let url = `wind?lat=${location.latitude}&lon=${location.longitude}`;
  if (date) {
    url += `&date=${format(date, "yyyy-MM-dd'T'HH:mm:ss")}`;
  }
  return await getData<Vector>(url);
};

export const getWindsDirection = async (
  locations: LocationModel[],
  checkpoints: number[],
  date: Date,
): Promise<Vector[]> => {
  console.log(locations);
  console.log(date);

  const latitudes: number[] = locations.map((location) => location.latitude);
  const longitudes: number[] = locations.map((location) => location.longitude);

  let url = `winds?lats=${JSON.stringify(latitudes)}&longs=${JSON.stringify(longitudes)}`;

  url += `&checkpoints=${JSON.stringify(checkpoints)}`;

  //let url = `winds?locs=${JSON.stringify(locations)}&checkpoints=${JSON.stringify(checkpoints)}`;
  if (date) {
    url += `&date=${format(date, "yyyy-MM-dd'T'HH:mm:ss")}`;
  }
  return await getData<Vector[]>(url);
};

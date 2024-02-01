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
  console.log(url);
  return await getData<Vector>(url);
};

import { LocationModel } from '../models/locationModel';
import { Vector } from '../models/vectorModel';
import { getData } from './utils';
import { format } from 'date-fns';

export const getTideDirection = async (
  location: LocationModel,
  date?: Date,
): Promise<Vector> => {
  let url = `tide?lat=${location.latitude}&lon=${location.longitude}`;
  if (date) {
    url += `&date=${format(date, "yyyy-MM-dd'T'HH:mm:ss")}`;
  }
  return await getData<Vector>(url);
};

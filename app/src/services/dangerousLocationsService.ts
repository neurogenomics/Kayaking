import { LocationModel } from '../models/locationModel';
import { getData } from './utils';
import { format } from 'date-fns';

export const getDangerousLocations = async (
  date?: Date,
): Promise<LocationModel[]> => {
  let url = 'windFiltering';
  if (date) {
    url += `?date=${format(date, "yyyy-MM-dd'T'HH:mm:ss")}`;
  }
  return await getData<LocationModel[]>(url);
};

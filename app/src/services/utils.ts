import { LocationModel } from '../models/locationModel';
import { getData } from './requestUtils';
import { format } from 'date-fns';

export const getSunset = async (
  location: LocationModel,
  date?: Date,
): Promise<SunsetInfo> => {
  let url = `sunset?lat=${location.latitude}&lng=${location.longitude}`;
  if (date) {
    url += `&date=${format(date, 'yyyy-MM-dd')}`;
  }
  const sunsetInfo = await getData<SunsetInfo>(url);
  return sunsetInfo;
};

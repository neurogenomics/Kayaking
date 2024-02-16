import { Double } from 'react-native/Libraries/Types/CodegenTypes';
import { LocationModel } from '../models/locationModel';
import { Route } from '../models/routeModel';
import { getData } from './utils';
import { format } from 'date-fns';

export const getRoute = async (
  startLocation: LocationModel,
  durationInMins: Double,
  date?: Date,
): Promise<Route[]> => {
  let url = `planRoute?lat=${startLocation.latitude}&lon=${startLocation.longitude}&duration=${durationInMins}`;
  if (date) {
    url += `&startDateTime=${format(date, "yyyy-MM-dd'T'HH:mm:ss")}`;
  }
  return await getData<Route[]>(url);
};

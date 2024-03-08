import { LocationModel } from '../models/locationModel';
import {
  ResolutionModel,
  WeatherGridModel,
  WeatherGridType,
} from '../models/weatherGridModel';
import { getData } from './utils';
import { format } from 'date-fns';

export const getWeatherGrid = async (
  type: WeatherGridType,
  fromLocation: LocationModel,
  toLocation: LocationModel,
  resolution: ResolutionModel,
  date?: Date,
): Promise<WeatherGridModel> => {
  let url = type === WeatherGridType.TIDE ? 'tideGrid' : 'windGrid';
  url += `?latFrom=${fromLocation.latitude}&latTo=${toLocation.latitude}&lonFrom=${fromLocation.longitude}&lonTo=${toLocation.longitude}&latRes=${resolution.latRes}&lonRes=${resolution.lonRes}`;
  if (date) {
    url += `&datetime=${format(date, "yyyy-MM-dd'T'HH:mm:ss")}`;
  }
  return await getData(url);
};

import { LocationModel } from '../models/locationModel';
import { GridModel, ResolutionModel } from '../models/gridModel';
import { getData } from './utils';
import { format } from 'date-fns';

export const getTideGrid = async (
  fromLocation: LocationModel,
  toLocation: LocationModel,
  resolution: ResolutionModel,
  date?: Date,
): Promise<GridModel> => {
  let url = `tideGrid?latFrom=${fromLocation.latitude}&latTo=${toLocation.latitude}&lonFrom=${fromLocation.longitude}&lonTo=${toLocation.longitude}&latRes=${resolution.latRes}&lonRes=${resolution.lonRes}`;
  if (date) {
    url += `&datetime=${format(date, "yyyy-MM-dd'T'HH:mm:ss")}`;
  }
  console.log(url);
  return await getData(url);
};

export const getWindGrid = async (
  fromLocation: LocationModel,
  toLocation: LocationModel,
  resolution: ResolutionModel,
  date?: Date,
): Promise<GridModel> => {
  let url = `windGrid?latFrom=${fromLocation.latitude}&latTo=${toLocation.latitude}&lonFrom=${fromLocation.longitude}&lonTo=${toLocation.longitude}&latRes=${resolution.latRes}&lonRes=${resolution.lonRes}`;
  if (date) {
    url += `&datetime=${format(date, "yyyy-MM-dd'T'HH:mm:ss")}`;
    // url += `&date=${format(date, "yyyy-MM-dd'T'HH:mm:ss")}`;
  }
  console.log(url);
  return await getData<GridModel>(url);
};

import { LocationModel } from '../models/locationModel';
import { GridModel, GridType, ResolutionModel } from '../models/gridModel';
import { getData } from './utils';
import { format } from 'date-fns';

export const getGrid = async (
  type: GridType,
  fromLocation: LocationModel,
  toLocation: LocationModel,
  resolution: ResolutionModel,
  date?: Date,
): Promise<GridModel> => {
  let url = type === GridType.TIDE ? 'tideGrid' : 'windGrid';
  console.log(url);
  url += `?latFrom=${fromLocation.latitude}&latTo=${toLocation.latitude}&lonFrom=${fromLocation.longitude}&lonTo=${toLocation.longitude}&latRes=${resolution.latRes}&lonRes=${resolution.lonRes}`;
  if (date) {
    url += `&datetime=${format(date, "yyyy-MM-dd'T'HH:mm:ss")}`;
  }
  console.log(url);
  return await getData(url);
};

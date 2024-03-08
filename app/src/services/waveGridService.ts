import { ResolutionModel } from '../models/weatherGridModel';
import { LocationModel } from '../models/locationModel';
import { format } from 'date-fns';
import { getData } from './utils';
import { WaveGridModel } from '../models/waveGridModel';

export const getWaveGrid = async (
  fromLocation: LocationModel,
  toLocation: LocationModel,
  resolution: ResolutionModel,
  date?: Date,
): Promise<WaveGridModel> => {
  let url = `waveGrid?latFrom=${fromLocation.latitude}&latTo=${toLocation.latitude}&lonFrom=${fromLocation.longitude}&lonTo=${toLocation.longitude}&latRes=${resolution.latRes}&lonRes=${resolution.lonRes}`;
  if (date) {
    url += `&datetime=${format(date, "yyyy-MM-dd'T'HH:mm:ss")}`;
  }
  return await getData(url);
};

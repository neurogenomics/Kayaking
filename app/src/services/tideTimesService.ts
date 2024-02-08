import { LocationModel } from '../models/locationModel';
import { TideInfo } from '../models/tideModel';
import { getData } from './utils';

export const getTideTimes = async (
  location: LocationModel,
): Promise<TideInfo> => {
  let url = `tidetimes?lat=${location.latitude}&lon=${location.longitude}`;
  const tideInfo = await getData<TideInfo>(url);
  return tideInfo;
};

import { LocationModel } from '../models/locationModel';
import { getData } from './utils';


export const getClosestSlipway = async (
  location: LocationModel,
): Promise<LocationModel> => {
  let url = `slipway?lat=${location.latitude}&lng=${location.longitude}`;
  const closestSlipway = await getData<LocationModel>(url);
  return closestSlipway;
};
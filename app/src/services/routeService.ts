import { Double } from 'react-native/Libraries/Types/CodegenTypes';
import { LocationModel } from '../models/locationModel';
import { RouteModel } from '../models/routeModel';
import { getData } from './utils';
import { format } from 'date-fns';
import { Region } from 'react-native-maps';

type BoundingBox = {
  latFrom: number;
  lonFrom: number;
  latTo: number;
  lonTo: number;
};

const regionToBoundingBox = (region: Region): BoundingBox => {
  const box: BoundingBox = {
    latFrom: region.latitude - region.latitudeDelta,
    lonFrom: region.longitude - region.longitudeDelta,
    latTo: region.latitude + region.latitudeDelta,
    lonTo: region.longitude + region.longitudeDelta,
  };
  return box;
};

export const getRoute = async (
  region: Region,
  durationInMins: Double,
  date?: Date,
): Promise<RouteModel[]> => {
  const boundingBox = regionToBoundingBox(region);
  let url = `planRoute?latFrom=${boundingBox.latFrom}&latTo=${boundingBox.latTo}&lonFrom=${boundingBox.lonFrom}&lonTo=${boundingBox.lonTo}&duration=${durationInMins}`;
  if (date) {
    url += `&startDateTime=${format(date, "yyyy-MM-dd'T'HH:mm:ss")}`;
  }
  return await getData<RouteModel[]>(url);
};

export const getCircularRoute = async (
  durationInMins: Double,
  date?: Date,
): Promise<RouteModel[]> => {
  let url = `planCircularRoute?duration=${60 * 3}`;
  url += '&startDateTime2024-03-07T17:15:35';
  if (date) {
    url += '&startDateTime2024-03-06T17:15:35';
    //   url += `&startDateTime=${format(date, "yyyy-MM-dd'T'HH:mm:ss")}`;
  }
  return await getData<RouteModel[]>(url);
};

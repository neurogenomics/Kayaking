import { Double } from 'react-native/Libraries/Types/CodegenTypes';
import { LocationModel } from '../models/locationModel';
import { RouteModel } from '../models/routeModel';
import { getData } from './utils';
import { format } from 'date-fns';
import { Region } from 'react-native-maps';
import { UserInput, getDuration } from '../models/userInputModel';

type BoundingBox = {
  latFrom: number;
  lonFrom: number;
  latTo: number;
  lonTo: number;
};

const regionToBoundingBox = (region: Region): BoundingBox => {
  const box: BoundingBox = {
    latFrom: region.latitude - region.latitudeDelta / 2,
    lonFrom: region.longitude - region.longitudeDelta / 2,
    latTo: region.latitude + region.latitudeDelta / 2,
    lonTo: region.longitude + region.longitudeDelta / 2,
  };
  console.log(box.latFrom + ', ' + box.lonFrom);
  console.log(box.latTo + ', ' + box.lonTo);
  return box;
};

export const getRoute = async (
  region: Region,
  userInput: UserInput,
): Promise<RouteModel[]> => {
  const boundingBox = regionToBoundingBox(region);
  let url = `planRoute?latFrom=${boundingBox.latFrom}&latTo=${boundingBox.latTo}&lonFrom=${boundingBox.lonFrom}&lonTo=${boundingBox.lonTo}&duration=${getDuration(userInput)}`;
  url += `&startDateTime=${format(userInput.startTime, "yyyy-MM-dd'T'HH:mm:ss")}`;
  return await getData<RouteModel[]>(url);
};

export const getCircularRoute = async (
  userInput: UserInput,
): Promise<RouteModel[]> => {
  let url = `planCircularRoute?duration=${getDuration(userInput)}`;
  url += `&startDateTime=${format(userInput.startTime, "yyyy-MM-dd'T'HH:mm:ss")}`;
  return await getData<RouteModel[]>(url);
};

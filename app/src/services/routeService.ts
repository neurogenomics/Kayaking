import { RouteModel } from '../models/routeModel';
import { getData } from './utils';
import { format } from 'date-fns';
import { Region } from 'react-native-maps';
import {
  PaddleSpeed,
  RouteDifficulty,
  RouteType,
  UserInput,
} from '../models/userInputModel';

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
  return box;
};

const paddleSpeedToString = (paddleSpeed: PaddleSpeed) => {
  switch (paddleSpeed) {
    case PaddleSpeed.Fast:
      return 'Fast';
    case PaddleSpeed.Normal:
      return 'Normal';
    case PaddleSpeed.Slow:
      return 'Slow';
  }
};

const difficultyToString = (difficulty: RouteDifficulty) => {
  switch (difficulty) {
    case RouteDifficulty.Easy:
      return 'Easy';
    case RouteDifficulty.Medium:
      return 'Medium';
    case RouteDifficulty.Hard:
      return 'Hard';
    case RouteDifficulty.Any:
      return 'Any';
  }
};

export const getRoute = async (
  region: Region,
  userInput: UserInput,
): Promise<RouteModel[]> => {
  let url =
    userInput.routeType === RouteType['Point-to-point']
      ? 'planRoute'
      : 'planCircularRoute';
  url += `?startDateTime=${format(userInput.startTime, "yyyy-MM-dd'T'HH:mm:ss")}`;
  url += `&duration=${userInput.duration}`;
  url += `&paddleSpeed=${paddleSpeedToString(userInput.paddleSpeed)}`;
  url += `&difficulty=${difficultyToString(userInput.routeDifficulty)}`;

  if (userInput.routeType === RouteType['Point-to-point']) {
    const boundingBox = regionToBoundingBox(region);
    url += `&latFrom=${boundingBox.latFrom}&latTo=${boundingBox.latTo}&lonFrom=${boundingBox.lonFrom}&lonTo=${boundingBox.lonTo}`;
  }

  const data = await getData<RouteModel[]>(url);
  return data.map((route) => ({
    ...route,
    startTime: new Date(route.startTime),
  }));
};

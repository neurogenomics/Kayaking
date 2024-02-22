import { LocationModel } from './locationModel';

export enum PaddleSpeed {
  Slow = 'slow',
  Normal = 'normal',
  Fast = 'fast',
}

export enum RouteType {
  PointToPoint = 'pointtopoint',
  Circular = 'circular',
}

export enum RouteDifficulty {
  Easy = 'easy',
  Medium = 'medium',
  Hard = 'hard',
}

export type UserInput = {
  location: LocationModel;
  startTime: Date;
  endTime: Date;
  paddleSpeed: PaddleSpeed;
  breakTime: Date;
  routeType: RouteType;
  routeDifficulty: RouteDifficulty;
};

// gets difference in hours between start time and end time
export const getDuration = (userInput: UserInput): number => {
  return (
    (userInput.endTime.getTime() - userInput.startTime.getTime()) / (1000 * 60)
  );
};

// gets length of break time
export const getBreakDuration = (userInput: UserInput): number => {
  return (
    userInput.breakTime.getTime() -
    new Date().setHours(0, 0, 0, 0) / (1000 * 60)
  );
};

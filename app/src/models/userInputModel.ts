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

export type UserInput = {
  location: LocationModel;
  startTime: Date;
  endTime: Date;
  paddleSpeed: PaddleSpeed;
  breakTime: number;
  routeType: RouteType;
};

export const getDuration = (userInput: UserInput): number => {
  return (
    (userInput.endTime.getTime() - userInput.startTime.getTime()) / (1000 * 60)
  );
};

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
  latitude: number;
  longitude: number;
  startTime: string;
  endTime: string;
  paddleSpeed: PaddleSpeed;
  breakTime: number;
  routeType: RouteType;
};

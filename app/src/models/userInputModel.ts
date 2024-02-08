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
  startTime: Date;
  endTime: Date;
  paddleSpeed: PaddleSpeed;
  breakTime: number;
  routeType: RouteType;
};

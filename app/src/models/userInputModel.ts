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
  Any = 'any',
  Easy = 'easy',
  Medium = 'medium',
  Hard = 'hard',
}

export type UserInput = {
  startTime: Date;
  duration: number;
  paddleSpeed: PaddleSpeed;
  routeType: RouteType;
  routeDifficulty: RouteDifficulty;
};

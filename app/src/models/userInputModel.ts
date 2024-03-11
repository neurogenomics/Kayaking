export enum PaddleSpeed {
  Slow = 'slow',
  Normal = 'normal',
  Fast = 'fast',
}

export enum RouteType {
  'Point-to-point' = 'point to point',
  Circular = 'circular',
}

export enum RouteDifficulty {
  Any = 'Any',
  Easy = 'Easy',
  Medium = 'Medium',
  Hard = 'Hard',
}

export type UserInput = {
  startTime: Date;
  duration: number;
  paddleSpeed: PaddleSpeed;
  routeType: RouteType;
  routeDifficulty: RouteDifficulty;
};

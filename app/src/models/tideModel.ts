export type TideInfo = {
  events: [TideEvent];
  source: TideStation;
};

export type TideStation = {
  id: string;
  name: string;
  location: Location;
};

export type TideEvent = {
  isHighTide: boolean;
  datetime: Date;
  height: number | null;
};

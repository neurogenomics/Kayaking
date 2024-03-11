import { LocationModel } from '../models/locationModel';
import { TideEvent, TideInfo } from '../models/tideModel';
import { getData } from './utils';

export const getTideTimes = async (
  location: LocationModel,
): Promise<TideInfo> => {
  const url = `tidetimes?lat=${location.latitude}&lon=${location.longitude}`;
  const tideInfo = await getData<TideInfo>(url);

  const parsedEvents: TideEvent[] = tideInfo.events.map((even) => ({
    ...even,
    datetime: new Date(even.datetime),
  }));

  const result: TideInfo = {
    events: parsedEvents,
    source: tideInfo.source,
  };

  return result;
};

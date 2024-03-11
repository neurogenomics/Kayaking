import { LocationModel } from '../../../src/models/locationModel';
import {
  ResolutionModel,
  WeatherGridType,
} from '../../../src/models/weatherGridModel';
import * as utils from '../../../src/services/utils';
import { format } from 'date-fns';
import { getData } from '../../../src/services/utils';
import { getWeatherGrid } from '../../../src/services/weatherGridService';

const getDataMock = jest.fn();

jest.spyOn(utils, 'getData').mockImplementation(getDataMock);

describe('getWeatherGrid', () => {
  const fromLocation: LocationModel = {
    latitude: 40.7128,
    longitude: -74.0064,
  };

  const toLocation: LocationModel = {
    latitude: 34.0522,
    longitude: -118.2437,
  };

  const resolution: ResolutionModel = {
    latRes: 1,
    lonRes: 1,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call getData with the correct URL for tide grid without date', async () => {
    const type = WeatherGridType.TIDE;
    await getWeatherGrid(type, fromLocation, toLocation, resolution);

    const expectedUrl = `tideGrid?latFrom=${fromLocation.latitude}&latTo=${toLocation.latitude}&lonFrom=${fromLocation.longitude}&lonTo=${toLocation.longitude}&latRes=${resolution.latRes}&lonRes=${resolution.lonRes}`;
    expect(getData).toHaveBeenCalledWith(expectedUrl);
  });

  it('should call getData with the correct URL for wind grid with date', async () => {
    const type = WeatherGridType.WIND;
    const date = new Date('2024-01-26');
    await getWeatherGrid(type, fromLocation, toLocation, resolution, date);

    const expectedUrl = `windGrid?latFrom=${fromLocation.latitude}&latTo=${toLocation.latitude}&lonFrom=${fromLocation.longitude}&lonTo=${toLocation.longitude}&latRes=${resolution.latRes}&lonRes=${resolution.lonRes}&datetime=${format(date, "yyyy-MM-dd'T'HH:mm:ss")}`;
    expect(getData).toHaveBeenCalledWith(expectedUrl);
  });
});

import { format } from 'date-fns';
import { LocationModel } from '../../../src/models/locationModel';
import { ResolutionModel } from '../../../src/models/weatherGridModel';
import { getWaveGrid } from '../../../src/services/waveGridService';
import * as utils from '../../../src/services/utils';

const getDataMock = jest.fn();

jest.spyOn(utils, 'getData').mockImplementation(getDataMock);

describe('getWaveGrid', () => {
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

  it('should call getData with the correct URL without date', async () => {
    await getWaveGrid(fromLocation, toLocation, resolution);

    const expectedUrl = `waveGrid?latFrom=${fromLocation.latitude}&latTo=${toLocation.latitude}&lonFrom=${fromLocation.longitude}&lonTo=${toLocation.longitude}&latRes=${resolution.latRes}&lonRes=${resolution.lonRes}`;
    expect(getDataMock).toHaveBeenCalledWith(expectedUrl);
  });

  it('should call getData with the correct URL with date', async () => {
    const date = new Date('2024-01-26');
    await getWaveGrid(fromLocation, toLocation, resolution, date);

    const expectedUrl = `waveGrid?latFrom=${fromLocation.latitude}&latTo=${toLocation.latitude}&lonFrom=${fromLocation.longitude}&lonTo=${toLocation.longitude}&latRes=${resolution.latRes}&lonRes=${resolution.lonRes}&datetime=${format(date, "yyyy-MM-dd'T'HH:mm:ss")}`;
    expect(getDataMock).toHaveBeenCalledWith(expectedUrl);
  });
});

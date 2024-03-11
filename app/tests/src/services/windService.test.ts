import { format } from 'date-fns';
import { LocationModel } from '../../../src/models/locationModel';
import * as utils from '../../../src/services/utils';
import {
  getWindDangerousWind,
  getWindDirection,
  getWindsDirection,
} from '../../../src/services/windService';

const getDataMock = jest.fn();

jest.spyOn(utils, 'getData').mockImplementation(getDataMock);
const postDataMock = jest.fn();

jest.spyOn(utils, 'postData').mockImplementation(postDataMock);

describe('getWindDirection', () => {
  const location: LocationModel = {
    latitude: 40.7128,
    longitude: -74.0064,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call getData with the correct URL without date', async () => {
    await getWindDirection(location);

    expect(getDataMock).toHaveBeenCalledWith('wind?lat=40.7128&lon=-74.0064');
  });

  it('should call getData with the correct URL with date', async () => {
    const date = new Date('2024-01-26');
    await getWindDirection(location, date);

    expect(getDataMock).toHaveBeenCalledWith(
      'wind?lat=40.7128&lon=-74.0064&date=2024-01-26T00:00:00',
    );
  });

  it('should return the vector', async () => {
    const vector = {
      u: 6.2,
      v: 8.6,
    };
    getDataMock.mockResolvedValue(vector);
    const result = await getWindDirection(location);
    expect(result).toEqual(vector);
  });
});
describe('getWindDangerousWind', () => {
  const locations: LocationModel[] = [
    { latitude: 40.7128, longitude: -74.0064 },
    { latitude: 34.0522, longitude: -118.2437 },
  ];
  const checkpoints = [1, 2, 3];
  const date = new Date('2024-01-26');

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call postData with the correct payload', async () => {
    await getWindDangerousWind(locations, checkpoints, date);

    expect(postDataMock).toHaveBeenCalledWith('dangerouswind', {
      locations,
      checkpoints,
      date: format(date, "yyyy-MM-dd'T'HH:mm:ss"),
    });
  });
});

describe('getWindsDirection', () => {
  const locations: LocationModel[] = [
    { latitude: 40.7128, longitude: -74.0064 },
    { latitude: 34.0522, longitude: -118.2437 },
  ];
  const checkpoints = [1, 2, 3];
  const date = new Date('2024-01-26');

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call postData with the correct payload', async () => {
    await getWindsDirection(locations, checkpoints, date);

    expect(postDataMock).toHaveBeenCalledWith('winds', {
      locations,
      checkpoints,
      date: format(date, "yyyy-MM-dd'T'HH:mm:ss"),
    });
  });
});

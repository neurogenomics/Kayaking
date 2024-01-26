import { LocationModel } from '../../../src/models/locationModel';
import * as utils from '../../../src/services/utils';
import { getTideDirection } from '../../../src/services/tideService';

const getDataMock = jest.fn();

jest.spyOn(utils, 'getData').mockImplementation(getDataMock);

describe('getTideDirection', () => {
  const location: LocationModel = {
    latitude: 40.7128,
    longitude: -74.0064,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call getData with the correct URL without date', async () => {
    await getTideDirection(location);

    expect(getDataMock).toHaveBeenCalledWith('tide?lat=40.7128&lon=-74.0064');
  });

  it('should call getData with the correct URL with date', async () => {
    const date = new Date('2024-01-26');
    await getTideDirection(location, date);

    expect(getDataMock).toHaveBeenCalledWith(
      'tide?lat=40.7128&lon=-74.0064&date=2024-01-26T00:00:00',
    );
  });

  it('should return the vector', async () => {
    const vector = {
      u: 6.2,
      v: 8.6,
    };
    getDataMock.mockResolvedValue(vector);
    const result = await getTideDirection(location);
    expect(result).toEqual(vector);
  });
});

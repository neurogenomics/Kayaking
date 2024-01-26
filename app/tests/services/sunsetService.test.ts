import { LocationModel } from '../../src/models/locationModel';
import * as utils from '../../src/services/utils';
import { getSunset } from '../../src/services/sunsetService';

// Mocking the getData function
const getDataMock = jest.fn();

jest.spyOn(utils, 'getData').mockImplementation(getDataMock);

describe('getSunset', () => {
  const location: LocationModel = {
    latitude: 40.7128,
    longitude: -74.0064,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call getData with the correct URL without date', async () => {
    await getSunset(location);

    expect(getDataMock).toHaveBeenCalledWith('sunset?lat=40.7128&lng=-74.0064');
  });

  it('should call getData with the correct URL with date', async () => {
    const date = new Date('2024-01-26');
    await getSunset(location, date);

    expect(getDataMock).toHaveBeenCalledWith(
      'sunset?lat=40.7128&lng=-74.0064&date=2024-01-26',
    );
  });

  it('should return the sunsetInfo from getData', async () => {
    const mockSunsetInfo: SunsetInfo = {
      sunrise: new Date('2024-01-26T12:00:00'),
      sunset: new Date('2024-01-26T15:00:00'),
    };

    getDataMock.mockResolvedValue(mockSunsetInfo);
    const result = await getSunset(location);
    expect(result).toEqual(mockSunsetInfo);
  });
});

import { LocationModel } from '../../src/models/locationModel';
import * as utils from '../../src/services/utils';
import { getClosestSlipway } from '../../src/services/slipwayService';

// Mocking the getData function
const getDataMock = jest.fn();

jest.spyOn(utils, 'getData').mockImplementation(getDataMock);

describe('getClosestSlipway', () => {
  const location: LocationModel = {
    latitude: 1.0721198,
    longitude: 50.688823,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call getData with the correct URL', async () => {
    await getClosestSlipway(location);

    expect(getDataMock).toHaveBeenCalledWith(
      'slipway?lat=1.0721198&lng=50.688823',
    );
  });

  it('should return the location from getData', async () => {
    const mockSunsetInfo: LocationModel = {
      latitude: 1.0721198,
      longitude: 50.688823,
    };

    getDataMock.mockResolvedValue(mockSunsetInfo);
    const result = await getClosestSlipway(location);
    expect(result).toEqual(mockSunsetInfo);
  });
});

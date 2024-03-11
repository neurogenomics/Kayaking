import { LocationModel } from '../../../src/models/locationModel';
import { TideEvent, TideInfo } from '../../../src/models/tideModel';
import { getTideTimes } from '../../../src/services/tideTimesService';
import * as utils from '../../../src/services/utils';

const getDataMock = jest.fn();

beforeAll(() => {
  jest.spyOn(utils, 'getData').mockImplementation(getDataMock);
});

describe('getTideTimes', () => {
  const mockLocation: LocationModel = {
    latitude: 40.7128,
    longitude: -74.0064,
  };

  const mockTideEvents: TideEvent[] = [
    {
      isHighTide: true,
      datetime: new Date('2024-01-01T12:00:00'),
      height: 3.5,
    },
    {
      isHighTide: false,
      datetime: new Date('2024-01-01T18:00:00'),
      height: 1.2,
    },
  ];

  const mockTideInfo: TideInfo = {
    events: mockTideEvents,
    source: {
      id: '3A',
      name: 'Test',
      location: mockLocation,
    },
  };

  beforeEach(() => {
    jest.clearAllMocks();
    getDataMock.mockResolvedValue(mockTideInfo);
  });

  it('should call getData with the correct URL', async () => {
    await getTideTimes(mockLocation);
    const expectedUrl = `tidetimes?lat=${mockLocation.latitude}&lon=${mockLocation.longitude}`;
    expect(getDataMock).toHaveBeenCalledWith(expectedUrl);
  });

  it('should return the tide events', async () => {
    const result = await getTideTimes(mockLocation);
    expect(result).toEqual(mockTideInfo);
  });
});

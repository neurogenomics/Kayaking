import { LocationModel } from '../../../src/models/locationModel';
import { TideEvent } from '../../../src/models/tideModel';
import { getTideTimes } from '../../../src/services/tideTimesService';
import * as utils from '../../../src/services/utils';

const getDataMock = jest.fn();

beforeAll(() => {
  jest.spyOn(utils, 'getData').mockImplementation(getDataMock);
});

describe('getTideTimes', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call getData with the correct URL', async () => {
    const location: LocationModel = {
      latitude: 40.7128,
      longitude: -74.0064,
    };
    await getTideTimes(location);
    const expectedUrl = `tidetimes?lat=${location.latitude}&lon=${location.longitude}`;
    expect(getDataMock).toHaveBeenCalledWith(expectedUrl);
  });

  it('should return the tide events', async () => {
    const location: LocationModel = {
      latitude: 40.7128,
      longitude: -74.0064,
    };
    const mockTideEvents: TideEvent[] = [
      {
        source: {
          id: '1',
          name: 'Tide Station 1',
          location: location,
        },
        datetime: new Date('2024-01-01T12:00:00'),
        height: 3.5,
      },
      {
        source: {
          id: '2',
          name: 'Tide Station 2',
          location: location,
        },
        datetime: new Date('2024-01-01T18:00:00'),
        height: 1.2,
      },
    ];
    getDataMock.mockResolvedValue(mockTideEvents);

    const result = await getTideTimes(location);
    expect(result).toEqual(mockTideEvents);
  });
});

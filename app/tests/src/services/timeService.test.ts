import { getWeatherDates } from '../../../src/services/timeService';
import * as utils from '../../../src/services/utils';

const getDataMock = jest.fn();

jest.spyOn(utils, 'getData').mockImplementation(getDataMock);

describe('getWeatherDates', () => {
  const mockResponse = ['2024-01-01', '2024-01-02', '2024-01-03'];
  beforeEach(() => {
    jest.clearAllMocks();
    getDataMock.mockResolvedValue(mockResponse);
  });

  it('should call getData with the correct URL', async () => {
    await getWeatherDates();
    expect(getDataMock).toHaveBeenCalledWith('times');
  });

  it('should return an array of Date objects', async () => {
    const result = await getWeatherDates();
    expect(result).toEqual([
      new Date('2024-01-01'),
      new Date('2024-01-02'),
      new Date('2024-01-03'),
    ]);
  });
});

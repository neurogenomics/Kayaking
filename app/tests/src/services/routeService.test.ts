import { Region } from 'react-native-maps';
import {
  UserInput,
  RouteType,
  PaddleSpeed,
  RouteDifficulty,
} from '../../../src/models/userInputModel';
import { getRoute } from '../../../src/services/routeService';
import * as utils from '../../../src/services/utils';
import { RouteModel } from '../../../src/models/routeModel';

const getDataMock = jest.fn();

beforeAll(() => {
  jest.spyOn(utils, 'getData').mockImplementation(getDataMock);
});

describe('getRoute', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    const mockRouteData: RouteModel[] = [];
    getDataMock.mockResolvedValue(mockRouteData);
  });

  it('should call getData with the correct URL for PointToPoint route', async () => {
    const region: Region = {
      latitude: 40.7128,
      longitude: -74.0064,
      latitudeDelta: 0.5,
      longitudeDelta: 0.5,
    };

    const userInput: UserInput = {
      routeType: RouteType.PointToPoint,
      startTime: new Date('2024-01-01T12:00:00'),
      duration: 60,
      paddleSpeed: PaddleSpeed.Normal,
      routeDifficulty: RouteDifficulty.Medium,
    };

    await getRoute(region, userInput);

    const expectedUrl =
      'planRoute?startDateTime=2024-01-01T12:00:00&duration=60&paddleSpeed=Normal&difficulty=Medium&latFrom=40.4628&latTo=40.9628&lonFrom=-74.2564&lonTo=-73.7564';
    expect(getDataMock).toHaveBeenCalledWith(expectedUrl);
  });

  it('should call getData with the correct URL for Circular route', async () => {
    const region: Region = {
      latitude: 40.7128,
      longitude: -74.0064,
      latitudeDelta: 0.5,
      longitudeDelta: 0.5,
    };

    const userInput: UserInput = {
      routeType: RouteType.Circular,
      startTime: new Date('2024-01-01T12:00:00'),
      duration: 60,
      paddleSpeed: PaddleSpeed.Fast,
      routeDifficulty: RouteDifficulty.Hard,
    };

    await getRoute(region, userInput);

    const expectedUrl =
      'planCircularRoute?startDateTime=2024-01-01T12:00:00&duration=60&paddleSpeed=Fast&difficulty=Hard';
    expect(getDataMock).toHaveBeenCalledWith(expectedUrl);
  });
});

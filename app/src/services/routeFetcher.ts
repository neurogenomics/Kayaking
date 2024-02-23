import { LocationModel } from '../models/locationModel';
import { RouteModel } from '../models/routeModel';
import { UserInput, getDuration } from '../models/userInputModel';
import { getRoute } from './routeService';

class RouteFetcher {
  private toFetch: [UserInput, LocationModel] | undefined;
  private isFetching = false;

  constructor(
    private setRoutes: React.Dispatch<
      React.SetStateAction<RouteModel[] | undefined>
    >,
  ) {}

  update(userInput: UserInput, locationModel: LocationModel) {
    console.log(locationModel, this.isFetching);
    if (this.isFetching) {
      this.toFetch = [userInput, locationModel];
    } else {
      void this.fetchRoutes(userInput, locationModel);
    }
  }

  private async fetchRoutes(
    userInput: UserInput,
    LocationModel: LocationModel,
  ) {
    this.isFetching = true;
    try {
      const routes: RouteModel[] = await getRoute(
        LocationModel,
        getDuration(userInput),
        userInput.startTime,
      );
      console.log('Fetched');
      this.setRoutes(routes);
    } catch (error) {
      console.log('Error getting routes: ', error);
      console.error(error);
    } finally {
      this.isFetching = false;
    }
    if (this.toFetch) {
      const userInput = this.toFetch[0];
      const location = this.toFetch[1];
      this.toFetch = undefined;
      await this.fetchRoutes(userInput, location);
    }
  }
}
export default RouteFetcher;

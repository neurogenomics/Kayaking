import { View, Text } from 'react-native';
import { RouteModel } from '../models/routeModel';

type RoutesProps = {
  routes: RouteModel[] | undefined;
  // TODO consider display if routes undefined
};
const Routes: React.FC<RoutesProps> = () => {
  return (
    <View>
      <Text>Route stuff goes here</Text>
    </View>
  );
};
export default Routes;

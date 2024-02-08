import { createStackNavigator } from '@react-navigation/stack';
import HomePage from './HomePage';
import { SlipwayMap } from './SlipwayMap';
import {MapVisualisation} from "./MapVisualisation";

const Stack = createStackNavigator();

const AppNavigator = () => {
  return (
    <Stack.Navigator>
      <Stack.Screen name="Home" component={HomePage} />
      <Stack.Screen name="Find a Slipway" component={SlipwayMap} />
      <Stack.Screen name="Tide Map" component={MapVisualisation} />
    </Stack.Navigator>
  );
};

export default AppNavigator;

import { RouteModel } from '../../models/routeModel';
import React from 'react';
import {
  createStackNavigator,
  StackNavigationProp,
} from '@react-navigation/stack';
import RoutesList from './RoutesList';
import RouteDetails, { RouteDetailsProps } from './RouteDetails';

type RootStackParamList = {
  RouteList: undefined;
  RouteDetails: { props: RouteDetailsProps };
};

export type RouteListNavigationProp = StackNavigationProp<
  RootStackParamList,
  'RouteList'
>;

type RoutesProps = {
  routes: RouteModel[] | undefined;
  selectedRouteIndex: number;
  setSelectedRouteIndex: React.Dispatch<React.SetStateAction<number>>;
  navigation: RouteListNavigationProp;
};

const Stack = createStackNavigator();

const Routes: React.FC<RoutesProps> = ({
  routes,
  selectedRouteIndex,
  setSelectedRouteIndex,
  navigation,
}) => {
  return (
    <Stack.Navigator>
      <Stack.Screen name="RouteList" options={{ headerShown: false }}>
        {() => (
          <RoutesList
            routes={routes}
            setSelectedRouteIndex={setSelectedRouteIndex}
            selectedRouteIndex={selectedRouteIndex}
            navigation={navigation}
          />
        )}
      </Stack.Screen>
      <Stack.Screen name="RouteDetails">
        {() => (
          <RouteDetails
            routes={routes}
            selectedRouteIndex={selectedRouteIndex}
          />
        )}
      </Stack.Screen>
    </Stack.Navigator>
  );
};
export default Routes;

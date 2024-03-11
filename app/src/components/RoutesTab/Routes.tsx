import { RouteModel } from '../../models/routeModel';
import React from 'react';
import {
  createStackNavigator,
  StackNavigationProp,
} from '@react-navigation/stack';
import RouteDetails from './RouteDetails';
import RoutesList from './RoutesList';
import { BottomSheetScrollView } from '@gorhom/bottom-sheet';

export type RoutesParamList = {
  RouteList: undefined;
  RouteDetails: {
    route: RouteModel;
    timeDisplayStr: string;
  };
};

export type RouteListNavigationProp = StackNavigationProp<
  RoutesParamList,
  'RouteList'
>;

type RoutesProps = {
  routes: RouteModel[];
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
    <Stack.Navigator initialRouteName={'RouteList'}>
      <Stack.Screen
        name="RouteList"
        options={{
          headerShown: false,
        }}
      >
        {() => (
          <BottomSheetScrollView>
            <RoutesList
              routes={routes}
              navigation={navigation}
              setSelectedRouteIndex={setSelectedRouteIndex}
              selectedRouteIndex={selectedRouteIndex}
            />
          </BottomSheetScrollView>
        )}
      </Stack.Screen>
      <Stack.Screen
        name={'RouteDetails'}
        options={{
          headerShown: false,
          cardStyle: { backgroundColor: 'white' },
        }}
        component={RouteDetails}
      ></Stack.Screen>
    </Stack.Navigator>
  );
};
export default Routes;

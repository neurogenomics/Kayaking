import React from 'react';
import { View, Text, Button } from 'react-native';
import { getDistance, RouteModel } from '../../models/routeModel';
import { RouteListNavigationProp } from './Routes';

export type RouteDetailsProps = {
  routes: RouteModel[] | undefined;
  selectedRouteIndex: number;
  navigation: RouteListNavigationProp;
};

const RouteDetails: React.FC<RouteDetailsProps> = ({
  routes,
  selectedRouteIndex,
  navigation,
}: RouteDetailsProps) => {
  if (routes === undefined) {
    return null;
  }
  return (
    <View>
      <Button
        title={'Back'}
        onPress={() => navigation.navigate('RouteList')}
      ></Button>
      <Text>{`Distance covered: ${getDistance(routes[selectedRouteIndex])}km`}</Text>
    </View>
  );
};

export default RouteDetails;

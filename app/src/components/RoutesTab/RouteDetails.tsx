import React from 'react';
import { View, Text } from 'react-native';
import { getDistance, RouteModel } from '../../models/routeModel';

export type RouteDetailsProps = {
  routes: RouteModel[] | undefined;
  selectedRouteIndex: number;
};

const RouteDetails: React.FC<RouteDetailsProps> = ({
  routes,
  selectedRouteIndex,
}: RouteDetailsProps) => {
  if (routes === undefined) {
    return null;
  }
  return (
    <View>
      <Text>{`Route ${selectedRouteIndex + 1}`}</Text>
      <Text>{`Distance covered: ${getDistance(routes[selectedRouteIndex])}km`}</Text>
    </View>
  );
};

export default RouteDetails;

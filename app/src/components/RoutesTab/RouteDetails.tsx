import React from 'react';
import { View, Text } from 'react-native';
import { getDistance, RouteModel } from '../../models/routeModel';

export type RouteDetailsProps = {
  selectedRoute: RouteModel | undefined;
  selectedRouteIndex: number;
};

const RouteDetails: React.FC<RouteDetailsProps> = ({
  selectedRoute,
  selectedRouteIndex,
}: RouteDetailsProps) => {
  if (!selectedRoute) {
    return null;
  }
  return (
    <View>
      <Text>{`Route ${selectedRouteIndex + 1}`}</Text>
      <Text>{`Distance covered: ${getDistance(selectedRoute)}km`}</Text>
    </View>
  );
};

export default RouteDetails;

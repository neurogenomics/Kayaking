import React, { useEffect } from 'react';
import { getDuration, UserInput } from '../../models/userInputModel';
import { Marker, Polyline } from 'react-native-maps';
import { getDistance, RouteModel } from '../../models/routeModel';
import { getRoute } from '../../services/routeService';
import { routeColors } from '../../colors';
import { StyleSheet, View } from 'react-native';

const styles = StyleSheet.create({
  selected: {
    strokeWidth: 5,
    strokeColor: routeColors.selected,
    zIndex: 3,
  },
  unselected: {
    strokeWidth: 2,
    strokeColor: routeColors.unselected,
    zIndex: 2,
  },
});

type RouteVisualisationProps = {
  userInput: UserInput;
  routes: RouteModel[] | undefined;
  setRoutes: React.Dispatch<React.SetStateAction<RouteModel[] | undefined>>;
  selectedRouteIndex: number;
  setSelectedRouteIndex: React.Dispatch<React.SetStateAction<number>>;
};

export const RouteVisualisation: React.FC<RouteVisualisationProps> = ({
  routes,
  selectedRouteIndex,
  setSelectedRouteIndex,
}: RouteVisualisationProps) => {
  return (
    <>
      {routes !== undefined && selectedRouteIndex < routes.length ? (
        // Plots a marker at the head of the selected route
        <Marker
          title={routes[selectedRouteIndex].name}
          description={`Distance covered: ${getDistance(routes[selectedRouteIndex])}km`}
          coordinate={routes[selectedRouteIndex].locations[0]}
          isPreselected={true}
        ></Marker>
      ) : null}
      {routes !== undefined
        ? routes.map((route, index) => (
            <View key={`polyline-${index}`}>
              <Polyline
                coordinates={route.locations.slice(
                  0,
                  route.locations.length / 2,
                )}
                strokeWidth={4}
                strokeColor={'red'}
                zIndex={1}
                tappable={true}
                onPress={() => setSelectedRouteIndex(index)}
              />
              <Polyline
                coordinates={route.locations.slice(route.locations.length / 2)}
                strokeWidth={2}
                strokeColor={'blue'}
                zIndex={2}
                tappable={true}
                onPress={() => setSelectedRouteIndex(index)}
              />
            </View>
          ))
        : null}
    </>
  );
};

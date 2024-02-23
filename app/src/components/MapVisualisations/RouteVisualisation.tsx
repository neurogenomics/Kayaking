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
  userInput,
  routes,
  setRoutes,
  selectedRouteIndex,
  setSelectedRouteIndex,
}: RouteVisualisationProps) => {
  const getRoutes = async (userInput: UserInput) => {
    try {
      const routes: RouteModel[] = await getRoute(
        userInput.location,
        getDuration(userInput),
        userInput.startTime,
      );
      setRoutes(routes);
    } catch (error) {
      console.log('Error getting routes: ', error);
      console.error(error);
    }
  };

  useEffect(() => {
    void getRoutes(userInput);
  }, [userInput, selectedRouteIndex]);

  return (
    <>
      {routes !== undefined && selectedRouteIndex < routes.length ? (
        // Plots a marker at the head of the selected route
        <Marker
          title={`Route ${selectedRouteIndex + 1}`}
          description={`Distance covered: ${getDistance(routes[selectedRouteIndex])}km`}
          coordinate={routes[selectedRouteIndex].locations[0]}
          isPreselected={true}
        ></Marker>
      ) : null}
      {routes !== undefined
        ? routes.map((route, index) => (
            <View key={`polyline-${index}`}>
              <Polyline
                coordinates={route.locations}
                strokeWidth={
                  index === selectedRouteIndex
                    ? styles.selected.strokeWidth
                    : styles.unselected.strokeWidth
                }
                strokeColor={
                  index === selectedRouteIndex
                    ? styles.selected.strokeColor
                    : styles.unselected.strokeColor
                }
                zIndex={
                  index === selectedRouteIndex
                    ? styles.selected.zIndex
                    : styles.unselected.zIndex
                }
                tappable={true}
                onPress={() => setSelectedRouteIndex(index)}
              />
            </View>
          ))
        : null}
    </>
  );
};

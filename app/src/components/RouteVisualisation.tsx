import React, { useEffect } from 'react';
import { getDuration, UserInput } from '../models/userInputModel';
import { Marker, Polyline } from 'react-native-maps';
import { RouteModel } from '../models/routeModel';
import { getRoute } from '../services/routeService';
import { routeColors } from '../colors';
import { View } from 'react-native';

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
    }
  };

  useEffect(() => {
    void getRoutes(userInput);
    console.log(selectedRouteIndex);
  }, [userInput, selectedRouteIndex]);

  return (
    <>
      {routes !== undefined && selectedRouteIndex < routes.length ? (
        <Marker
          title={`Route ${selectedRouteIndex + 1}`}
          description={`Distance covered: ${(routes[selectedRouteIndex].length / 1000).toFixed(2)}km`}
          coordinate={routes[selectedRouteIndex].locations[0]}
        ></Marker>
      ) : null}
      {routes !== undefined
        ? routes.map((route, index) => (
            <View key={`polyline-${index}`}>
              <Polyline
                coordinates={route.locations}
                strokeWidth={index === selectedRouteIndex ? 5 : 2}
                strokeColor={
                  index === selectedRouteIndex
                    ? routeColors.selected
                    : routeColors.unselected
                }
                zIndex={index === selectedRouteIndex ? 3 : 2}
                tappable={true}
                onPress={() => setSelectedRouteIndex(index)}
              />
            </View>
          ))
        : null}
    </>
  );
};

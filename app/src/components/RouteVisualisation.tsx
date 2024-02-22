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
      console.log('length');
      console.log(routes.length);
      setRoutes(routes);
    } catch (error) {
      console.log('Error getting routes: ', error);
    }
  };

  useEffect(() => {
    void getRoutes(userInput);
  }, [userInput]);

  return (
    <>
      {routes
        ? routes.map((route, index) => (
            <View key={`polyline-${index}`}>
              {index === selectedRouteIndex ? (
                <Marker
                  title={`Route ${index + 1}`}
                  description={`Distance covered: ${Math.round(route.length / 1000)}km`}
                  coordinate={route.locations[0]}
                ></Marker>
              ) : null}
              <Polyline
                coordinates={route.locations}
                strokeWidth={index === selectedRouteIndex ? 5 : 2}
                strokeColor={
                  index === selectedRouteIndex
                    ? routeColors.selected
                    : routeColors.unselected
                }
                onPress={() => setSelectedRouteIndex(index)}
              />
            </View>
          ))
        : null}
    </>
  );
};

import React, { useEffect } from 'react';
import { getDuration, UserInput } from '../models/userInputModel';
import { Marker, Polyline } from 'react-native-maps';
import { RouteModel } from '../models/routeModel';
import { getRoute } from '../services/routeService';

type RouteVisualisationProps = {
  userInput: UserInput;
  routes: RouteModel[] | undefined;
  setRoutes: React.Dispatch<React.SetStateAction<RouteModel[] | undefined>>;
};

export const RouteVisualisation: React.FC<RouteVisualisationProps> = ({
  userInput,
  routes,
  setRoutes,
}: RouteVisualisationProps) => {
  const colors = ['red', 'yellow', 'green', 'blue', 'pink'];

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
    } catch (error) {
      console.log('Error getting routes: ', error);
    }
  };

  useEffect(() => {
    void getRoutes(userInput);
  }, [userInput]);

  return (
    <>
      {routes ? (
        routes.map((route, index) => (
          <>
            <Marker
              key={index + 1}
              title={`Route ${index + 1}`}
              description={`Distance covered: ${Math.round(route.length / 1000)}km`}
              coordinate={route.locations[0]}
            ></Marker>
            <Polyline
              key={index}
              coordinates={route.locations}
              strokeWidth={2}
              strokeColor={colors[index % 5]}
            />
          </>
        ))
      ) : (
        <></>
      )}
    </>
  );
};

import React, { useEffect, useState } from 'react';
import { getDuration, UserInput } from '../models/userInputModel';
import { Marker, Polyline } from 'react-native-maps';
import { RouteModel } from '../models/routeModel';
import { getRoute } from '../services/routeService';

type RouteVisualisationProps = {
  userInput: UserInput;
};

export const RouteVisualisation: React.FC<RouteVisualisationProps> = ({
  userInput,
}: RouteVisualisationProps) => {
  const [routes, setRoutes] = useState<RouteModel[]>();
  const colors = ['red', 'yellow', 'green', 'blue', 'pink'];

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
  }, [userInput]);

  return (
    <>
      {routes ? (
        routes.map((route, index) => (
          <>
            <Marker
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

import React from 'react';
import { UserInput } from '../../models/userInputModel';
import { Polyline, Region } from 'react-native-maps';
import { RouteModel } from '../../models/routeModel';
import {
  LocationModel,
  angleBetweenLocations,
} from '../../models/locationModel';
import { Vector, normalVector } from '../../models/vectorModel';
import { routeVisualisationColors } from '../../colors';

type RouteVisualisationProps = {
  userInput: UserInput;
  routes: RouteModel[];
  selectedRouteIndex: number;
  region: Region;
  setSelectedRouteIndex: React.Dispatch<React.SetStateAction<number>>;
};

export const RouteVisualisation: React.FC<RouteVisualisationProps> = ({
  routes,
  selectedRouteIndex,
  setSelectedRouteIndex,
}: RouteVisualisationProps) => {
  const offsetLocation = (
    location: LocationModel,
    index: number,
    direction: Vector,
  ): LocationModel => {
    return {
      latitude: location.latitude + index * 0.0005 * direction.u,
      longitude: location.longitude + index * 0.0005 * direction.v,
    };
  };

  const duplicateLocationCount: Map<string, number> = new Map();

  const offsetRoutes = routes.map((route) => {
    const start = route.locations[0];
    const end = route.locations[route.locations.length - 1];

    const angle = angleBetweenLocations(start, end);
    const normal = normalVector(angle);

    const offsetLocations = route.locations.slice(1, -1).map((location) => {
      const locationKey = JSON.stringify(location);
      const count = duplicateLocationCount.get(locationKey) ?? 0;
      const newLocation = offsetLocation(location, count, normal);
      duplicateLocationCount.set(locationKey, count + 1);
      return newLocation;
    });
    return {
      ...route,
      locations: [start, ...offsetLocations, end],
    };
  });

  return (
    <>
      {offsetRoutes.map((route, index) => (
        <Polyline
          key={`polyline-${index}`}
          coordinates={route.locations}
          strokeColor={
            routeVisualisationColors[index % routeVisualisationColors.length]
          }
          tappable={true}
          strokeWidth={selectedRouteIndex === index ? 4 : 2}
          onPress={() => setSelectedRouteIndex(index)}
        />
      ))}
    </>
  );
};

import React, { useEffect, useState } from 'react';
import { UserInput } from '../../models/userInputModel';
import { Marker, Polyline, Region } from 'react-native-maps';
import { RouteModel } from '../../models/routeModel';
import {
  LocationModel,
  angleBetweenLocations,
  calculateDistanceBetweenLocations,
} from '../../models/locationModel';
import { Vector, unitVector } from '../../models/vectorModel';
import { routeVisualisationColors } from '../../colors';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faSkullCrossbones } from '@fortawesome/free-solid-svg-icons';
import { getWindDangerousWind } from '../../services/windService';

type RouteVisualisationProps = {
  userInput: UserInput;
  routes: RouteModel[];
  selectedRouteIndex: number;
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
    const normal = unitVector(angle);

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

  const filterCoordinates = (coordinates: LocationModel[]): LocationModel[] => {
    const filteredCoordinates: LocationModel[] = [];

    if (coordinates.length > 0) {
      filteredCoordinates.push(coordinates[0]);
    }
    for (let i = 1; i < coordinates.length; i++) {
      let shouldAddCoord = true;
      for (let j = 0; j < filteredCoordinates.length; j++) {
        if (
          calculateDistanceBetweenLocations(
            filteredCoordinates[j],
            coordinates[i],
          ) < 500
        ) {
          shouldAddCoord = false;
          break;
        }
      }
      if (shouldAddCoord) {
        filteredCoordinates.push(coordinates[i]);
      }
    }

    return filteredCoordinates;
  };

  const potentiallyDangerousAreas = filterCoordinates(
    Array.from(duplicateLocationCount.keys()).map((locationKey) => {
      const location = JSON.parse(locationKey) as LocationModel;
      return location;
    }),
  );

  const [dangerousAreas, setDangerousAreas] = useState<LocationModel[]>([]);

  useEffect(() => {
    if (routes.length > 0) {
      getWindDangerousWind(
        potentiallyDangerousAreas,
        // TODO consider the times the routes are actually at the place
        Array(potentiallyDangerousAreas.length).fill(0) as number[],
        routes[0].startTime,
      )
        .then((wind) => {
          setDangerousAreas(
            potentiallyDangerousAreas.filter((area, index) => {
              return wind[index];
            }),
          );
        })
        .catch((err) => console.error(err));
    }
  }, [routes]);

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
      {dangerousAreas.map((location, index) => (
        <Marker
          key={`marker-${index}`}
          coordinate={location}
          title="Strong wind out to sea"
        >
          <FontAwesomeIcon icon={faSkullCrossbones} />
        </Marker>
      ))}
    </>
  );
};

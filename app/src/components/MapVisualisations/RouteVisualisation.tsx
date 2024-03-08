import React, { useEffect, useState } from 'react';
import { UserInput } from '../../models/userInputModel';
import { Callout, Marker, Polyline } from 'react-native-maps';
import { RouteModel } from '../../models/routeModel';
import {
  LocationModel,
  angleBetweenLocations,
  calculateDistanceBetweenLocations,
} from '../../models/locationModel';
import { Vector, unitVector } from '../../models/vectorModel';
import { routeVisualisationColors } from '../../colors';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faTriangleExclamation } from '@fortawesome/free-solid-svg-icons';
import { getWindDangerousWind } from '../../services/windService';
import { View, Text, StyleSheet } from 'react-native';
import { differenceInSeconds, format } from 'date-fns';
import { getEarliestTime } from './utils';

type RouteVisualisationProps = {
  userInput: UserInput;
  routes: RouteModel[];
  selectedRouteIndex: number;
  setSelectedRouteIndex: React.Dispatch<React.SetStateAction<number>>;
  showWindWarnings: boolean;
};

type IndexCheckpoint = {
  index: number;
  checkpoint: number;
};

type LocationIndexCheckpoints = {
  location: LocationModel;
  indexCheckpoints: IndexCheckpoint[];
};

const styles = StyleSheet.create({
  circle: {
    width: 16,
    height: 16,
    borderRadius: 8,
    marginRight: 4,
  },
  row: {
    flexDirection: 'row',
    flex: 1,
  },
});

export const RouteVisualisation: React.FC<RouteVisualisationProps> = ({
  routes,
  selectedRouteIndex,
  setSelectedRouteIndex,
  showWindWarnings,
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

  const duplicateLocationIndicies: Map<string, IndexCheckpoint[]> = new Map();

  const offsetRoutes = routes.map((route, index) => {
    const start = route.locations[0];
    const end = route.locations[route.locations.length - 1];

    const angle = angleBetweenLocations(start, end);
    const normal = unitVector(angle);

    const offsetLocations: LocationModel[] = [];
    for (let i = 1; i < route.locations.length - 1; i++) {
      const location = route.locations[i];
      const checkpoint = route.checkpoints[i];
      const locationKey = JSON.stringify(location);
      const duplicateIndicies =
        duplicateLocationIndicies.get(locationKey) ?? [];
      const newLocation = offsetLocation(
        location,
        duplicateIndicies.length,
        normal,
      );
      duplicateIndicies.push({ index, checkpoint });
      duplicateLocationIndicies.set(locationKey, duplicateIndicies);
      offsetLocations.push(newLocation);
    }
    return {
      ...route,
      locations: [start, ...offsetLocations, end],
    };
  });

  // Filters LocationIndexCheckpoints by removing any that are too close to eachotehr
  const removeNearbyCoordinates = (
    locationIndexCheckpoints: LocationIndexCheckpoints[],
    minDistance: number,
  ): LocationIndexCheckpoints[] => {
    const filteredCoordinates: LocationIndexCheckpoints[] = [];

    if (locationIndexCheckpoints.length > 0) {
      filteredCoordinates.push(locationIndexCheckpoints[0]);
    }
    for (let i = 1; i < locationIndexCheckpoints.length; i++) {
      let shouldAddCoord = true;
      for (let j = 0; j < filteredCoordinates.length; j++) {
        if (
          calculateDistanceBetweenLocations(
            filteredCoordinates[j].location,
            locationIndexCheckpoints[i].location,
          ) < minDistance
        ) {
          shouldAddCoord = false;
          break;
        }
      }
      if (shouldAddCoord) {
        filteredCoordinates.push(locationIndexCheckpoints[i]);
      }
    }

    return filteredCoordinates;
  };

  const potentiallyDangerousAreas = removeNearbyCoordinates(
    Array.from(duplicateLocationIndicies.entries()).map(
      ([locationKey, indexCheckpoints]) => {
        const location = JSON.parse(locationKey) as LocationModel;
        const locationAndIndicies: LocationIndexCheckpoints = {
          location,
          indexCheckpoints,
        };

        return locationAndIndicies;
      },
    ),
    500,
  );

  const [dangerousAreas, setDangerousAreas] = useState<
    LocationIndexCheckpoints[]
  >([]);

  useEffect(() => {
    if (!showWindWarnings) {
      setDangerousAreas([]);
      return;
    }
    if (routes.length > 0) {
      const earliestDate = getEarliestTime(
        routes.map((route) => route.startTime),
      );

      const locations: LocationModel[] = [];
      const checkpoints: number[] = [];

      for (let i = 0; i < potentiallyDangerousAreas.length; i++) {
        const locationAndIndicies = potentiallyDangerousAreas[i];
        for (let j = 0; j < locationAndIndicies.indexCheckpoints.length; j++) {
          locations.push(locationAndIndicies.location);
          checkpoints.push(
            routes[locationAndIndicies.indexCheckpoints[j].index].checkpoints[
              j
            ],
          );
        }
      }

      getWindDangerousWind(locations, checkpoints, earliestDate)
        .then((wind) => {
          let index = 0;

          const dangerousAreas: LocationIndexCheckpoints[] = [];
          for (let i = 0; i < potentiallyDangerousAreas.length; i++) {
            const locationAndIndicies = potentiallyDangerousAreas[i];

            const indexCheckpoints: IndexCheckpoint[] = [];

            for (
              let j = 0;
              j < locationAndIndicies.indexCheckpoints.length;
              j++
            ) {
              if (wind[index]) {
                const routeIndex =
                  locationAndIndicies.indexCheckpoints[j].index;
                const secondsOffset = differenceInSeconds(
                  earliestDate,
                  routes[routeIndex].startTime,
                );

                const indexCheckpoint: IndexCheckpoint = {
                  index: routeIndex,
                  checkpoint:
                    locationAndIndicies.indexCheckpoints[j].checkpoint +
                    secondsOffset,
                };
                indexCheckpoints.push(indexCheckpoint);
              }
              index += 1;
            }

            if (indexCheckpoints.length > 0) {
              const result: LocationIndexCheckpoints = {
                location: locationAndIndicies.location,
                indexCheckpoints,
              };

              dangerousAreas.push(result);
            }
          }
          setDangerousAreas(dangerousAreas);
        })
        .catch((err) => console.error(err));
    }
  }, [routes, showWindWarnings]);

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
      {dangerousAreas.map((dangerousArea, index) => (
        <Marker key={`marker-${index}`} coordinate={dangerousArea.location}>
          <FontAwesomeIcon icon={faTriangleExclamation} color="yellow" />
          <Callout>
            <View style={{ height: 100, width: 200 }}>
              <Text>Winds blowing out to sea!</Text>

              {dangerousArea.indexCheckpoints.map((indexCheckpoint, index) => {
                const route = routes[indexCheckpoint.index];
                const checkpoint = indexCheckpoint.checkpoint;
                const newDate = new Date(route.startTime.getTime());
                newDate.setSeconds(newDate.getSeconds() + checkpoint);
                return route ? (
                  <View key={index} style={styles.row}>
                    <View
                      style={[
                        styles.circle,
                        {
                          backgroundColor:
                            routeVisualisationColors[
                              indexCheckpoint.index %
                                routeVisualisationColors.length
                            ],
                        },
                      ]}
                    ></View>
                    <Text>Affected At: {format(newDate, 'HH:mm')}</Text>
                  </View>
                ) : null;
              })}
            </View>
          </Callout>
        </Marker>
      ))}
    </>
  );
};

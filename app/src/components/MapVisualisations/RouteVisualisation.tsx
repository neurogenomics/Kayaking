import React, { useEffect, useState } from 'react';
import { UserInput } from '../../models/userInputModel';
import { Callout, Marker, Polyline, Region } from 'react-native-maps';
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
import { Modal } from 'react-native-paper';
import { View, Text, StyleSheet } from 'react-native';
import { format } from 'date-fns';

type RouteVisualisationProps = {
  userInput: UserInput;
  routes: RouteModel[];
  selectedRouteIndex: number;
  setSelectedRouteIndex: React.Dispatch<React.SetStateAction<number>>;
  showWindWarnings: boolean;
};

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

  type IndexCheckpoint = {
    index: number;
    checkpoint: number;
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

  type LocationAndIndicies = {
    location: LocationModel;
    indicies: number[];
    checkpoints: number[];
  };

  const filterCoordinates = (
    locationAndIndicies: LocationAndIndicies[],
  ): LocationAndIndicies[] => {
    const filteredCoordinates: LocationAndIndicies[] = [];

    if (locationAndIndicies.length > 0) {
      filteredCoordinates.push(locationAndIndicies[0]);
    }
    for (let i = 1; i < locationAndIndicies.length; i++) {
      let shouldAddCoord = true;
      for (let j = 0; j < filteredCoordinates.length; j++) {
        if (
          calculateDistanceBetweenLocations(
            filteredCoordinates[j].location,
            locationAndIndicies[i].location,
          ) < 500
        ) {
          shouldAddCoord = false;
          break;
        }
      }
      if (shouldAddCoord) {
        filteredCoordinates.push(locationAndIndicies[i]);
      }
    }

    return filteredCoordinates;
  };

  const potentiallyDangerousAreas = filterCoordinates(
    Array.from(duplicateLocationIndicies.entries()).map(
      ([locationKey, indiciesCheckpoints]) => {
        const location = JSON.parse(locationKey) as LocationModel;

        const locationAndIndicies: LocationAndIndicies = {
          location,
          indicies: indiciesCheckpoints.map((it) => it.index),
          checkpoints: indiciesCheckpoints.map((it) => it.checkpoint),
        };

        return locationAndIndicies;
      },
    ),
  );

  function getEarliestTime(dates: Date[]): Date {
    return dates.reduce((earliest, current) =>
      current < earliest ? current : earliest,
    );
  }

  const [dangerousAreas, setDangerousAreas] = useState<LocationAndIndicies[]>(
    [],
  );

  const differenceInSeconds = (earlier: Date, later: Date): number => {
    return (later.getTime() - earlier.getTime()) / 1000;
  };

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
        for (let j = 0; j < locationAndIndicies.checkpoints.length; j++) {
          locations.push(locationAndIndicies.location);
          checkpoints.push(
            routes[locationAndIndicies.indicies[j]].checkpoints[j],
          );
        }
      }

      getWindDangerousWind(locations, checkpoints, earliestDate)
        .then((wind) => {
          let index = 0;

          const dangerousAreas: LocationAndIndicies[] = [];
          for (let i = 0; i < potentiallyDangerousAreas.length; i++) {
            const locationAndIndicies = potentiallyDangerousAreas[i];

            const indicies: number[] = [];
            const checkpoints: number[] = [];

            for (let j = 0; j < locationAndIndicies.checkpoints.length; j++) {
              if (wind[index]) {
                const routeIndex = locationAndIndicies.indicies[j];
                const secondsOffset = differenceInSeconds(
                  earliestDate,
                  routes[routeIndex].startTime,
                );

                indicies.push(locationAndIndicies.indicies[j]);
                checkpoints.push(
                  locationAndIndicies.checkpoints[j] + secondsOffset,
                );
              }
              index += 1;
            }

            if (indicies.length > 0) {
              const result: LocationAndIndicies = {
                location: locationAndIndicies.location,
                indicies,
                checkpoints,
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
        <Marker
          key={`marker-${index}`}
          coordinate={dangerousArea.location}
          title={`indicies: ${dangerousArea.indicies.toString()} times: ${dangerousArea.checkpoints.toString()}`}
        >
          <FontAwesomeIcon icon={faTriangleExclamation} color="yellow" />
          <Callout>
            <View style={{ height: 100, width: 200 }}>
              <Text>Winds blowing out to sea!</Text>

              {dangerousArea.indicies.map((routeIndex, index) => {
                const route = routes[routeIndex];
                const checkpoint = dangerousArea.checkpoints[index];
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
                              routeIndex % routeVisualisationColors.length
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

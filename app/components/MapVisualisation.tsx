import MapView, { Marker, Polyline } from 'react-native-maps';
import { StyleSheet, View } from 'react-native';
import React, { useState, useEffect } from 'react';
import { Route } from '../src/models/routeModel';
import { UserInput } from '../src/models/userInputModel';
import { LocationModel } from '../src/models/locationModel';
import { getRoute } from '../src/services/routeService';
import { getTideGrid, getWindGrid } from '../src/services/gridService';
import { GridModel, ResolutionModel } from '../src/models/gridModel';
import arrow from '../assets/arrow.png';
import { Image } from 'react-native';

type SlipwayMapProps = {
  navigation;
  route: {
    params: { user: UserInput };
  };
};

type CoordRotation = {
  coord: {
    longitude: number;
    latitude: number;
  };
  direction: number;
};

export const MapVisualisation: React.FC<SlipwayMapProps> = ({
  navigation,
  route,
}) => {
  const [routes, setRoutes] = useState<Route[]>();
  const [coords, setCoords] = useState<CoordRotation[]>();
  const [grid, setGrid] = useState<GridModel>();

  const isleOfWightLocation = {
    longitude: -1.33,
    latitude: 50.67,
    longitudeDelta: 0.56,
    latitudeDelta: 0.22,
  };

  useEffect(() => {
    getRoutes(route.params.user);
    getGrid();
  }, []);

  const getGrid = async () => {
    try {
      const fromLocation: LocationModel = {
        latitude: 49.37,
        longitude: -1.8,
      };
      const toLocation: LocationModel = {
        latitude: 51,
        longitude: 0,
      };
      const resolution: ResolutionModel = {
        latRes: 0.1,
        lonRes: 0.1,
      };
      const today = new Date();
      const yesterday = new Date(today - 1 * 24 * 60 * 60 * 1000);
      const tomorrow = new Date();
      const date = new Date(2024, 1, 11, 8, 0, 0);

      const grid = await getTideGrid(
        fromLocation,
        toLocation,
        resolution,
      );
      //setGrid(grid);
      //console.log(Objec(grid));
      console.log(grid[0]);

      const markers: CoordRotation[] = [];
      for (let i = 0; i < grid.latIndex.length; i++) {
        for (let j = 0; j < grid.lonIndex.length; j++) {
          console.log(grid.grid[i][j])
          if (grid.grid[i][j].u !== 0.69) {
            const direction = rotate(grid.grid[i][j].u, grid.grid[i][j].v);
            const coord: CoordRotation = {
              coord: {
                latitude: grid.latIndex[i],
                longitude: grid.lonIndex[j],
              },
              direction: direction,
            };
            markers.push(coord);
          }
        }
      }
      setCoords(markers);
      console.log(coords);
    } catch (error) {
      console.log('Error getting grid: ', error);
    }
  };

  const getRoutes = async (user: UserInput) => {
    try {
      const location: LocationModel = {
        latitude: user.latitude,
        longitude: user.longitude,
      };
      const startDate: Date = new Date(user.startTime);
      const endDate: Date = new Date(user.endTime);
      const duration: number = (endDate - startDate) / (1000 * 60);
      console.log(duration);
      const routes: Route[] = await getRoute(location, duration, startDate);
      setRoutes(routes);
    } catch (error) {
      console.error('Error getting routes: ', error);
    }
  };

  const rotate = (u: number, v: number) => {
    const angleRadians = Math.atan2(u, v);
    let angleDegrees = angleRadians * (180 / Math.PI);
    if (angleDegrees < 0) {
      angleDegrees += 360;
    }
    angleDegrees = Math.round(angleDegrees);
    console.log(`rotate(${angleDegrees}deg)`);
    return angleDegrees;
  };

  const colours = ['blue', 'red', 'green', 'pink', 'yellow'];

  return (
    <View style={styles.mapContainer}>
      <MapView style={styles.map} initialRegion={isleOfWightLocation}>
        {coords ? (
          coords.map((coord, index) => (
            <View
              key={index}
              style={{ width: 30, height: 30, objectFit: 'contain' }}
            >
              <Marker coordinate={coord.coord}>
                <Image
                  source={arrow}
                  style={{
                    width: 30,
                    height: 30,
                    resizeMode: 'contain',
                    backgroundColor: 'transparent',
                    transform: `rotate(${coord.direction}deg)`,
                    opacity: 0.5,
                  }}
                />
              </Marker>
            </View>
          ))
        ) : (
          <View />
        )}
        {routes ? (
          routes.map((route, index) => (
            <View key={index}>
              <Marker
                title={`Route ${index + 1}`}
                description={`Distance covered: ${Math.round(route.length / 1000)}km`}
                coordinate={route.locations[0]}
              />
              <Polyline
                key={index}
                coordinates={route.locations}
                strokeWidth={2}
                strokeColor={colours[index % 5]}
              />
            </View>
          ))
        ) : (
          <View />
        )}
      </MapView>
    </View>
  );
};

const styles = StyleSheet.create({
  mapContainer: {
    flex: 1,
    height: '100%',
    width: '100%',
    alignItems: 'center',
    justifyContent: 'center',
  },
  map: {
    flex: 1,
    height: '50%',
    width: '100%',
    alignItems: 'center',
    justifyContent: 'center',
  },
});

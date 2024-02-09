import MapView, { Marker, Polyline } from 'react-native-maps';
import { StyleSheet, View, TouchableOpacity, Text } from 'react-native';
import React, { useState, useEffect } from 'react';
import { Route } from '../src/models/routeModel';
import { UserInput } from '../src/models/userInputModel';
import { Modalize } from 'react-native-modalize';
import { LocationModel } from '../src/models/locationModel';
import { getRoute } from '../src/services/routeService';
import {RoutesSlideUp} from "./RoutesSlideUp";

type SlipwayMapProps = {
  navigation;
  route: {
    params: { user: UserInput };
  };
};

export const MapVisualisation: React.FC<SlipwayMapProps> = ({
  navigation,
  route,
}) => {
  const [routes, setRoutes] = useState<Route[]>();

  const isleOfWightLocation = {
    longitude: -1.33,
    latitude: 50.67,
    longitudeDelta: 0.56,
    latitudeDelta: 0.22,
  };

  useEffect(() => {
    console.log(route.params.user);
    getRoutes(route.params.user);
  }, []);

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
    const rad: number = Math.atan2(v, u);
    const deg: number = rad * (180 / Math.PI); // convert radians to degrees
    return `rotate(${deg}deg)`;
  };

  const colours = ['blue', 'red', 'green', 'pink', 'yellow'];

  return (
    <View style={styles.mapContainer}>
      <MapView style={styles.map} initialRegion={isleOfWightLocation}>
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
      {/*<RoutesSlideUp {navigation, routes} />*/}
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

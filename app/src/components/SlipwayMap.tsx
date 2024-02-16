import MapView, { Marker, MarkerSelectEvent } from 'react-native-maps';
import { Button, StyleSheet, View } from 'react-native';
import React, { useState } from 'react';
// TODO: get rid of this once Elsa's beach data is merged
import beachData from './fakeBeachData.json';
import { PaddleSpeed, RouteType, UserInput } from '../models/userInputModel';
import { StackNavigationHelpers } from '@react-navigation/stack/lib/typescript/src/types';
import { isleOfWight } from '../../constants';

type SlipwayMapProps = {
  navigation: StackNavigationHelpers;
  route: {
    params: { startTime: string; endTime: string; paddleSpeed: PaddleSpeed };
  };
};

export const SlipwayMap: React.FC<SlipwayMapProps> = ({
  navigation,
  route,
}) => {
  const [latitude, setLatitude] = useState(0);
  const [longitude, setLongitude] = useState(0);

  const handleMarkerSelect = (event: MarkerSelectEvent): void => {
    setLatitude(event.nativeEvent.coordinate.latitude);
    setLongitude(event.nativeEvent.coordinate.longitude);
  };

  const findRoute = () => {
    const user: UserInput = {
      latitude: latitude,
      longitude: longitude,
      startTime: route.params.startTime,
      endTime: route.params.endTime,
      paddleSpeed: route.params.paddleSpeed,
      breakTime: 0,
      routeType: RouteType.PointToPoint,
    };
    navigation.navigate('Choose a Route', { user });
  };

  return (
    <View style={styles.mapContainer}>
      <Button title={'Generate routes'} onPress={findRoute}></Button>
      <MapView style={styles.map} initialRegion={isleOfWight}>
        {beachData.map((beach, index) => (
          <Marker
            key={index}
            coordinate={beach.coordinate}
            title={beach.title}
            description={beach.description}
            onSelect={handleMarkerSelect}
          ></Marker>
        ))}
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

import MapView, { Marker } from 'react-native-maps';
import { Button, StyleSheet, View } from 'react-native';
import React, { useState } from 'react';
import beachData from './fakeBeachData.json';
import {
  PaddleSpeed,
  RouteType,
  UserInput,
} from '../src/models/userInputModel';

type SlipwayMapProps = {
  navigation;
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

  const handleMarkerSelect = (event): void => {
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
    console.log('USERINPUT');
    console.log(user);
    navigation.navigate('Choose a Route', { user });
  };

  const isleOfWight = {
    longitude: -1.33,
    latitude: 50.67,
    longitudeDelta: 0.56,
    latitudeDelta: 0.22,
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

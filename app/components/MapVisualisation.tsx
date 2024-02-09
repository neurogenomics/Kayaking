import MapView, { Marker, Polyline } from 'react-native-maps';
import { Image, StyleSheet, View } from 'react-native';
import React, { useEffect } from 'react';
import windData from './fakeWindData.json';
import arrow from '../assets/arrow.png';
import { UserInput } from '../src/models/userInputModel';

export const MapVisualisation: React.FC<UserInput> = () => {
  const isleOfWightLocation = {
    longitude: -1.33,
    latitude: 50.67,
    longitudeDelta: 0.56,
    latitudeDelta: 0.22,
  };

  const rotate = (u: number, v: number) => {
    const rad: number = Math.atan2(v, u);
    const deg: number = rad * (180 / Math.PI); // convert radians to degrees
    return `rotate(${deg}deg)`;
  };

  const sampleCoordinates = [
    { latitude: 50.7051, longitude: -1.2929 }, // Ryde
    { latitude: 50.7037, longitude: -1.2986 }, // Seaview
    { latitude: 50.7067, longitude: -1.313 }, // Bembridge
    { latitude: 50.6691, longitude: -1.1554 }, // Sandown
    { latitude: 50.741, longitude: -1.1567 }, // Yaverland
    { latitude: 50.6939, longitude: -1.3047 }, // Newport
  ];

  return (
    <View style={styles.mapContainer}>
      <MapView style={styles.map} initialRegion={isleOfWightLocation}>
        <Polyline coordinates={sampleCoordinates} strokeWidth={2} strokeColor={'blue'}></Polyline>
        {/*<View>*/}
        {/*  {windData.map((wind, index) => (*/}
        {/*    <Marker*/}
        {/*      key={index}*/}
        {/*      coordinate={{*/}
        {/*        latitude: wind.latitude,*/}
        {/*        longitude: wind.longitude,*/}
        {/*      }}*/}
        {/*    >*/}
        {/*      <Image*/}
        {/*        source={arrow}*/}
        {/*        style={{*/}
        {/*          width: 30,*/}
        {/*          height: 30,*/}
        {/*          resizeMode: 'contain',*/}
        {/*          backgroundColor: 'transparent',*/}
        {/*          transform: [*/}
        {/*            { rotate: rotate(wind.wind_data[1], wind.wind_data[0]) },*/}
        {/*          ],*/}
        {/*        }}*/}
        {/*      ></Image>*/}
        {/*    </Marker>*/}
        {/*  ))}*/}
        {/*</View>*/}
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

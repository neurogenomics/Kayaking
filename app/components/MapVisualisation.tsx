import MapView, { Marker } from 'react-native-maps';
import { Image, StyleSheet, View } from 'react-native';
import React from 'react';
import windData from './fakeWindData.json';
import arrow from '../assets/arrow.png';

interface MapVisualisationProps {
  lon: number;
  lat: number;
}

export const MapVisualisation: React.FC<MapVisualisationProps> = () => {
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

  return (
    <View style={styles.mapContainer}>
      <MapView style={styles.map} initialRegion={isleOfWightLocation}>
        <View>
          {windData.map((wind, index) => (
            <Marker
              key={index}
              coordinate={{
                latitude: wind.latitude,
                longitude: wind.longitude,
              }}
            >
              <Image
                source={arrow}
                style={{
                  width: 30,
                  height: 30,
                  resizeMode: 'contain',
                  backgroundColor: 'transparent',
                  transform: [
                    { rotate: rotate(wind.wind_data[1], wind.wind_data[0]) },
                  ],
                }}
              ></Image>
            </Marker>
          ))}
        </View>
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

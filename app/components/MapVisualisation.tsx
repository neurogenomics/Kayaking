import MapView from 'react-native-maps';
import { StyleSheet, View } from 'react-native';
import React from 'react';

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
  return (
    <View style={styles.mapContainer}>
      <MapView style={styles.map} initialRegion={isleOfWightLocation} />
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


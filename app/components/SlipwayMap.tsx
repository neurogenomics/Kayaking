import MapView, { Marker } from 'react-native-maps';
import { Button, StyleSheet, View } from 'react-native';
import React, { useState } from 'react';
import beachData from './fakeBeachData.json';

interface Beach {
  title: string;
  description: string;
  coordinate: {
    latitude: number;
    longitude: number;
  };
}

export const SlipwayMap: React.FC<{ navigation }> = ({ navigation }) => {
  const handleMarkerSelect = (marker: Beach): void => {
    // TODO do something when you select the marker
    console.log(marker);
  };

  const goToTideMap = () => {
    navigation.navigate('Tide Map');
  };

  const isleOfWight = {
    longitude: -1.33,
    latitude: 50.67,
    longitudeDelta: 0.56,
    latitudeDelta: 0.22,
  };

  return (
    <View style={styles.mapContainer}>
      <Button title={'Go to tide map'} onPress={goToTideMap}></Button>
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

import { StyleSheet, Text, TextInput, View } from 'react-native';
import React, { useState } from 'react';
import DisplayResponse from './DisplayResponse';

const BACKEND_URL = 'http://0.0.0.0:8080'; // TODO: use .env variables

export const SunsetInfo: React.FC = () => {
  const [lat, setLat] = useState('38.907192');
  const [lng, setLng] = useState('-77.036873');
  return (
    <View style={styles.container}>
      <Text>Sunset data</Text>
      <View style={styles.rowContainer}>
        <Text>Latitude: </Text>
        <TextInput onChangeText={setLat} value={lat} style={styles.input} />
      </View>
      <View style={styles.rowContainer}>
        <Text>Longitude: </Text>
        <TextInput onChangeText={setLng} value={lng} style={styles.input} />
      </View>
      <DisplayResponse
        url={`${BACKEND_URL}/sunset?lat=${lat}&lng=${lng}`}
      ></DisplayResponse>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
  rowContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    margin: 4,
  },
  input: {
    backgroundColor: 'lightgrey',
    borderWidth: 1,
    padding: 1,
    marginLeft: 5,
  },
});

export default SunsetInfo;

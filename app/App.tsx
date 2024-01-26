import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import React, { useEffect, useState } from 'react';
import { getSunset } from './src/services/utils';
import { LocationModel } from './src/models/locationModel';

export const App: React.FC = () => {
  const [info, setInfo] = useState('No data yet');

  const update = async () => {
    const location: LocationModel = {
      latitude: 50,
      longitude: 23,
    };

    const date = new Date();

    const sunset: SunsetInfo = await getSunset(location, date);
    setInfo(sunset.sunset);
  };

  useEffect(() => {
    void update();
  }, []);
  return (
    <View style={styles.container}>
      <Text>Welcome to the Kayak App</Text>
      <StatusBar style="auto" />
      <Text>{info}</Text>
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
});

export default App;

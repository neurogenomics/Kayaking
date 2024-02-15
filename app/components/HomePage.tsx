import React, { useState } from 'react';
import { Button, Image, StyleSheet, Text, View } from 'react-native';
import { StackNavigationHelpers } from '@react-navigation/stack/lib/typescript/src/types';
import { StartEndTimePicker } from './StartEndTimePicker';
import { PaddleSpeedButtons } from './PaddleSpeedButtons';
import { PaddleSpeed } from '../src/models/userInputModel';

export const HomePage: React.FC<{ navigation: StackNavigationHelpers }> = ({
  navigation,
}) => {
  const now: Date = new Date();
  const later: Date = new Date();
  later.setHours(later.getHours() + 1);
  const [startTime, setStartTime] = useState(now);
  const [endTime, setEndTime] = useState(later);
  const [paddleSpeed, setPaddleSpeed] = useState(PaddleSpeed.Normal);

  const handleSaveLocation = () => {
    navigation.navigate('Choose a Slipway', {
      startTime: startTime.toISOString(),
      endTime: endTime.toISOString(),
      paddleSpeed,
    });
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Welcome, Kayaker!</Text>
      <Image source={require('../assets/kayaking.png')} style={styles.image} />
      <StartEndTimePicker
        startTime={startTime}
        setStartTime={setStartTime}
        endTime={endTime}
        setEndTime={setEndTime}
      />
      <PaddleSpeedButtons
        paddleSpeed={paddleSpeed}
        setPaddleSpeed={setPaddleSpeed}
      />
      <Button title="Find a Slipway!" onPress={handleSaveLocation} />
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
  title: {
    fontSize: 32,
    margin: 5,
    textAlign: 'center',
  },
  image: { width: 200, height: 200 },
});

export default HomePage;

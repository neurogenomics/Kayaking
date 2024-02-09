import React, { useState } from 'react';
import { Button, Image, StyleSheet, Text, View } from 'react-native';
import kayakingImage from '../assets/kayaking.png';
import { StackNavigationHelpers } from '@react-navigation/stack/lib/typescript/src/types';
import { StartEndTimePicker } from './StartEndTimePicker';
import { PaddleSpeedButtons } from './PaddleSpeedButtons';
import { PaddleSpeed } from '../src/models/userInputModel';

export const HomePage: React.FC<{ navigation: StackNavigationHelpers }> = ({
  navigation,
}) => {
  const [startTime, setStartTime] = useState(new Date());
  const [endTime, setEndTime] = useState(new Date());
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
      <Image source={kayakingImage} style={{ width: 200, height: 200 }} />
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
});

export default HomePage;

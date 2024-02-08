import React, { useState } from 'react';
import { View, Text, Image, Button, StyleSheet } from 'react-native';
import kayakingImage from '../assets/kayaking.png';
export const HomePage: React.FC<{ navigation }> = ({ navigation }) => {
  const handleSaveLocation = () => {
    navigation.navigate('Find a Slipway');
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Welcome, Kayaker!</Text>
      <Image source={kayakingImage} style={{ width: 200, height: 200 }} />
      <Button title="Get started!" onPress={handleSaveLocation} />
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
    fontSize: 20,
    margin: 5,
    textAlign: 'center',
  },
});

export default HomePage;

// HomePage.tsx
import React, { useState } from 'react';
import { View, Text, Image, Button, StyleSheet, TextInput } from 'react-native';
import kayakingImage from '../assets/kayaking.png';
import { useNavigation } from '@react-navigation/native';

const HomePage: React.FC = () => {
  const [longitude, setLongitude] = useState('');
  const [latitude, setLatitude] = useState('');
  const navigation = useNavigation();

  const InputLocation = ({ setLongitude, setLatitude }) => {
    return (
      <View style={styles.locationContainer}>
        <TextInput
          style={styles.locationInput}
          enterKeyHint="enter"
          placeholder="Longitude"
          keyboardType="numeric"
          returnKeyType="done"
          onChangeText={setLongitude}
        />
        <TextInput
          style={styles.locationInput}
          enterKeyHint="enter"
          placeholder="Latitude"
          keyboardType="numeric"
          returnKeyType="done"
          onChangeText={setLatitude}
        />
      </View>
    );
  };

  const handleSaveLocation = () => {
    // Do something with the longitude and latitude values
    console.log('Longitude:', longitude);
    console.log('Latitude:', latitude);

    // Navigate to the new screen and pass data as params
    navigation.navigate('DisplayDataScreen', { longitude, latitude });
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Welcome, Kayaker!</Text>
      <Image source={kayakingImage} style={{ width: 200, height: 200 }} />
      <Text style={styles.title}>Enter your location below:</Text>
      <InputLocation setLongitude={setLongitude} setLatitude={setLatitude} />
      <Button title="Save Location" onPress={handleSaveLocation} />
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
  locationContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 16,
  },
  locationInput: {
    width: '30%', // Adjust the width as needed
    borderBottomWidth: 1,
    padding: 8,
  },
});

export default HomePage;

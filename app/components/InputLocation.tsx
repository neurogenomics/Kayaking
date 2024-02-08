import React from 'react';
import { View, TextInput, StyleSheet } from 'react-native';

export const InputLocation = ({ setLongitude, setLatitude }) => {
  return (
    <View style={styles.locationContainer}>
      <TextInput
        style={styles.locationInput}
        placeholder="Longitude"
        returnKeyType="done"
        keyboardType="numbers-and-punctuation"
        onChangeText={setLongitude}
      />
      <TextInput
        style={styles.locationInput}
        enterKeyHint="enter"
        placeholder="Latitude"
        returnKeyType="done"
        keyboardType="numbers-and-punctuation"
        onChangeText={setLatitude}
      />
    </View>
  );
};

const styles = StyleSheet.create({
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

export default InputLocation;

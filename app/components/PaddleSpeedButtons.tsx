import React from 'react';
import { PaddleSpeed } from '../src/models/userInputModel';
import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';

export const PaddleSpeedButtons: React.FC<{
  paddleSpeed: PaddleSpeed;
  setPaddleSpeed: React.Dispatch<React.SetStateAction<PaddleSpeed>>;
}> = ({ paddleSpeed, setPaddleSpeed }) => {
  const paddleSpeedOptions = [
    { type: PaddleSpeed.Slow, name: 'Slow' },
    { type: PaddleSpeed.Normal, name: 'Normal' },
    { type: PaddleSpeed.Fast, name: 'Fast' },
  ];
  return (
    <View style={styles.paddleButtonContainer}>
      <Text style={styles.label}>Select Paddle Speed</Text>
      <View style={styles.buttonContainer}>
        {paddleSpeedOptions.map((option) => (
          <TouchableOpacity
            key={option.name}
            style={[
              styles.button,
              paddleSpeed === option.type && styles.selectedButton,
            ]}
            onPress={() => {
              setPaddleSpeed(option.type);
            }}
          >
            <Text style={styles.buttonText}>{option.name}</Text>
          </TouchableOpacity>
        ))}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  label: {
    fontSize: 18,
    marginBottom: 5,
    margin: 10,
  },
  paddleButtonContainer: {
    flexDirection: 'column',
    alignItems: 'center',
  },
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
  },
  button: {
    backgroundColor: '#f0f0f0',
    padding: 10,
    borderRadius: 5,
    borderWidth: 1,
    borderColor: '#ccc',
    marginTop: 0,
    margin: 10,
  },
  selectedButton: {
    backgroundColor: 'lightblue',
    borderColor: 'blue',
    color: 'white',
  },
  buttonText: {
    fontSize: 16,
  },
});

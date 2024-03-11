import React from 'react';
//import { PaddleSpeed } from '../models/userInputModel';
import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { colors } from '../../colors';

// Define the Option interface
interface Option<T> {
  type: T;
  name: string;
}

// Define Props as a generic type accepting any enum
interface Props<T> {
  label: string;
  options: Option<T>[];
  selectedOption: T;
  onSelect: React.Dispatch<React.SetStateAction<T>>;
}

// Utility function to generate options array from enum values
export function generateOptions<T>(enumObject: Record<string, T>): Option<T>[] {
  return Object.keys(enumObject).map((key) => ({
    type: enumObject[key],
    name: key,
  }));
}

export const SelectButtons: React.FC<Props<string>> = ({
  label,
  options,
  selectedOption,
  onSelect,
}) => {
  return (
    <View style={styles.paddleButtonContainer}>
      <View style={styles.labelContainer}>
        <Text style={styles.label}>{label}</Text>
      </View>
      <View
        style={
          options.length > 3
            ? styles.largebuttonContainer
            : styles.buttonContainer
        }
      >
        {options.map((option) => (
          <TouchableOpacity
            key={option.name}
            style={[
              styles.button,
              selectedOption === option.type && styles.selectedButton,
            ]}
            onPress={() => {
              onSelect(option.type);
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
  labelContainer: {
    flex: 1,
  },
  label: {
    fontSize: 14,
    marginBottom: 5,
    fontWeight: 'bold',
  },
  paddleButtonContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    flex: 2,
  },
  largebuttonContainer: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    flex: 4,
  },
  button: {
    backgroundColor: '#f0f0f0',
    padding: 10,
    borderRadius: 5,
    borderWidth: 1,
    borderColor: '#ccc',
  },
  selectedButton: {
    backgroundColor: colors.green.highlight,
    borderColor: colors.green.medium,
    color: 'white',
  },
  buttonText: {
    fontSize: 16,
  },
});

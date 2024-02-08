import React, { useState } from 'react';
import {
  Button,
  Image,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import kayakingImage from '../assets/kayaking.png';
import DateTimePicker from '@react-native-community/datetimepicker';
import {
  PaddleSpeed,
  RouteType,
  UserInput,
} from '../src/models/userInputModel';

const PaddleSpeedButtons: React.FC = () => {
  const paddleSpeedOptions = ['Slow', 'Normal', 'Fast'];
  const [selectedOption, setSelectedOption] = useState(null);

  const handleOptionSelect = (option) => {
    setSelectedOption(option);
  };

  return (
    <View style={styles.paddleButtonContainer}>
      <Text style={styles.label}>Select Paddle Speed</Text>
      <View style={styles.buttonContainer}>
        {paddleSpeedOptions.map((option) => (
          <TouchableOpacity
            key={option}
            style={[
              styles.button,
              selectedOption === option && styles.selectedButton,
            ]}
            onPress={() => handleOptionSelect(option)}
          >
            <Text style={styles.buttonText}>{option}</Text>
          </TouchableOpacity>
        ))}
      </View>
    </View>
  );
};

export const HomePage: React.FC<{ navigation }> = ({ navigation }) => {
  const handleSaveLocation = () => {
    navigation.navigate('Find a Slipway');
  };

  const userInput: UserInput = {
    startTime: new Date(),
    endTime: new Date(),
    paddleSpeed: PaddleSpeed.Normal,
    breakTime: 0,
    routeType: RouteType.PointToPoint,
  };

  const [startTime, setStartTime] = useState(userInput.startTime);
  const [showEndTimePicker, setShowEndTimePicker] = useState(false);
  const handleStartTimeChange = (event, selectedDate) => {
    const currentDate: Date = selectedDate;
    setStartTime(currentDate);
    setShowEndTimePicker(true);
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Welcome, Kayaker!</Text>
      <Image source={kayakingImage} style={{ width: 200, height: 200 }} />
      <View style={styles.timePickerContainer}>
        <View style={styles.pickerContainer}>
          <Text style={styles.label}>Enter Start Time</Text>
          <DateTimePicker
            value={userInput.startTime}
            mode="time"
            is24Hour={true}
            minimumDate={userInput.startTime}
            onChange={handleStartTimeChange}
          ></DateTimePicker>
        </View>
        <View style={styles.pickerContainer}>
          <Text style={styles.label}>Enter End Time</Text>
          <DateTimePicker
            value={userInput.endTime}
            mode="time"
            is24Hour={true}
            minimumDate={startTime}
            disabled={!showEndTimePicker}
          ></DateTimePicker>
        </View>
      </View>
      <PaddleSpeedButtons />
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
  timePickerContainer: {
    flexDirection: 'row', // Arrange components horizontally
    justifyContent: 'space-evenly', // Evenly space components
    paddingHorizontal: 20, // Add padding for better spacing
  },
  pickerContainer: {
    marginBottom: 20,
    alignItems: 'center',
  },
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
  },
  buttonText: {
    fontSize: 16,
    fontWeight: 'bold',
  },
  sliderContainer: {
    flexDirection: 'column',
    alignItems: 'center',
  },
});

export default HomePage;

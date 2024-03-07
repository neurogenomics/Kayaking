import { ScrollView, StyleSheet, Text, View } from 'react-native';
import DateTimePicker, {
  DateTimePickerEvent,
} from '@react-native-community/datetimepicker';
import React, { useEffect, useState } from 'react';
import {
  PaddleSpeed,
  RouteDifficulty,
  RouteType,
  UserInput,
} from '../../models/userInputModel';
import { generateOptions, SelectButtons } from './SelectionButtons';
import { Button } from 'react-native-paper';

type FiltersProps = {
  setUserInput: React.Dispatch<React.SetStateAction<UserInput>>;
  onFindRoutesPressed: () => void;
};

export const Filters: React.FC<FiltersProps> = ({
  setUserInput,
  onFindRoutesPressed,
}: FiltersProps) => {
  const [startTime, setStartTime] = useState<Date>(new Date());

  const [duration, setDuration] = useState<Date>(new Date(0, 0, 0, 2, 0, 0, 0));
  const [paddleSpeed, setPaddleSpeed] = useState<PaddleSpeed>(
    PaddleSpeed.Normal,
  );
  const [routeType, setRouteType] = useState<RouteType>(RouteType.PointToPoint);
  const [routeDifficulty, setRouteDifficulty] = useState<RouteDifficulty>(
    RouteDifficulty.Medium,
  );

  const paddleSpeedOptions = generateOptions(PaddleSpeed);
  const routeTypeOptions = generateOptions(RouteType);
  const routeDifficultyOptions = generateOptions(RouteDifficulty);

  useEffect(() => {
    setUserInput((prevUserInput) => ({
      ...prevUserInput,
      startTime: startTime,
      duration: duration.getMinutes() + duration.getHours() * 60,
      paddleSpeed: paddleSpeed,
      routeType: routeType,
      routeDifficulty: routeDifficulty,
    }));
  }, [startTime, duration, paddleSpeed, routeType, routeDifficulty]);

  return (
    <View style={styles.container}>
      <Button mode="contained" onPress={onFindRoutesPressed}>
        <Text>Find Routes</Text>
      </Button>
      <View style={styles.divider}></View>
      <View style={styles.rowContainer}>
        <View style={styles.column}>
          <Text style={styles.title}>Start Date</Text>
          <DateTimePicker
            value={startTime}
            mode="date"
            display="default"
            onChange={(
              _event: DateTimePickerEvent,
              selectedStartTime: Date,
            ) => {
              if (_event.type === 'set') {
                setStartTime(selectedStartTime);
              }
            }}
          />
        </View>
        <View style={styles.column}>
          <Text style={styles.title}>Start Time</Text>
          <DateTimePicker
            value={startTime}
            mode="time"
            display="default"
            onChange={(
              _event: DateTimePickerEvent,
              selectedStartTime: Date,
            ) => {
              if (_event.type === 'set') {
                setStartTime(selectedStartTime);
              }
            }}
          />
        </View>
        <View style={styles.column}>
          <Text style={styles.title}>Duration</Text>
          <DateTimePicker
            value={duration}
            mode="time"
            display="default"
            onChange={(
              _event: DateTimePickerEvent,
              selectedStartTime: Date,
            ) => {
              if (_event.type === 'set') {
                setDuration(selectedStartTime);
              }
            }}
          />
        </View>
      </View>
      <View style={styles.divider}></View>
      <SelectButtons
        label={'Paddle Speed'}
        options={paddleSpeedOptions}
        selectedOption={paddleSpeed}
        onSelect={setPaddleSpeed}
      />
      <View style={styles.divider}></View>
      <SelectButtons
        label={'Route Difficulty'}
        options={routeDifficultyOptions}
        selectedOption={routeDifficulty}
        onSelect={setRouteDifficulty}
      />
      <View style={styles.divider}></View>
      <SelectButtons
        label={'Route Type'}
        options={routeTypeOptions}
        selectedOption={routeType}
        onSelect={setRouteType}
      />
    </View>
  );
};

export default Filters;

const styles = StyleSheet.create({
  container: {
    marginVertical: 10,
    marginHorizontal: 20,
  },
  timePickerContainer: {
    flexDirection: 'row',
    justifyContent: 'space-evenly',
    paddingHorizontal: 20,
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
  rowContainer: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
  },
  column: {
    alignItems: 'center',
    flex: 1,
  },
  title: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  divider: {
    height: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.1)',
    marginVertical: 10,
  },
});

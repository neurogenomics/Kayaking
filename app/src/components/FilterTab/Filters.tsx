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
import { StartEndTimePicker } from './StartEndTimePicker';
import { eastCowes } from '../../../constants';

type FiltersProps = {
  setUserInput: React.Dispatch<React.SetStateAction<UserInput>>;
};

export const Filters: React.FC<FiltersProps> = ({
  setUserInput,
}: FiltersProps) => {
  const [startTime, setStartTime] = useState<Date>(new Date());
  const endDate = new Date();
  endDate.setHours(endDate.getHours() + 1);
  const [endTime, setEndTime] = useState<Date>(endDate);
  const [paddleSpeed, setPaddleSpeed] = useState<PaddleSpeed>(
    PaddleSpeed.Normal,
  );
  const [routeType, setRouteType] = useState<RouteType>(RouteType.PointToPoint);
  const [routeDifficulty, setRouteDifficulty] = useState<RouteDifficulty>(
    RouteDifficulty.Medium,
  );
  const [breakDuration, setBreakDuration] = useState(new Date(0));

  const onBreakDurationChange = (
    _event: DateTimePickerEvent,
    selectedBreakDuration: Date,
  ) => {
    const currentBreak: Date = selectedBreakDuration || breakDuration;
    setBreakDuration(currentBreak);
  };

  const paddleSpeedOptions = generateOptions(PaddleSpeed);
  const routeTypeOptions = generateOptions(RouteType);
  const routeDifficultyOptions = generateOptions(RouteDifficulty);

  useEffect(() => {
    setUserInput((prevUserInput) => ({
      ...prevUserInput,
      location: eastCowes, // TODO: change this once we have location from map
      startTime: startTime,
      endTime: endTime,
      paddleSpeed: paddleSpeed,
      routeType: routeType,
      routeDifficulty: routeDifficulty,
      breakTime: breakDuration,
    }));
  }, [
    startTime,
    endTime,
    paddleSpeed,
    routeType,
    routeDifficulty,
    breakDuration,
  ]);

  return (
    <View style={styles.container}>
      <View style={styles.rowContainer}>
        <View style={styles.column}>
          <Text style={styles.title}>Start Date</Text>
          <DateTimePicker value={breakDuration} mode="date" display="default" />
        </View>
        <View style={styles.column}>
          <Text style={styles.title}>Start Time</Text>
          <DateTimePicker value={breakDuration} mode="time" display="default" />
        </View>
        <View style={styles.column}>
          <Text style={styles.title}>Duration</Text>
          <DateTimePicker value={breakDuration} mode="time" display="default" />
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

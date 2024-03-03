import { StyleSheet, Text, View } from 'react-native';
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
    <View>
      <StartEndTimePicker
        startTime={startTime}
        setStartTime={setStartTime}
        endTime={endTime}
        setEndTime={setEndTime}
      />
      <SelectButtons
        label={'Select paddle speed'}
        options={paddleSpeedOptions}
        selectedOption={paddleSpeed}
        onSelect={setPaddleSpeed}
      />
      <SelectButtons
        label={'Select route difficulty'}
        options={routeDifficultyOptions}
        selectedOption={routeDifficulty}
        onSelect={setRouteDifficulty}
      />
      <SelectButtons
        label={'Select route type'}
        options={routeTypeOptions}
        selectedOption={routeType}
        onSelect={setRouteType}
      />
      <View style={styles.pickerContainer}>
        <Text style={styles.label}>Break time duration</Text>
        <DateTimePicker
          value={breakDuration}
          mode="time"
          is24Hour={true}
          onChange={onBreakDurationChange}
        />
      </View>
    </View>
  );
};

export default Filters;

const styles = StyleSheet.create({
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
});

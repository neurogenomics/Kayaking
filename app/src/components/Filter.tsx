import { StyleSheet, Text, View } from 'react-native';
import DateTimePicker, {
  DateTimePickerEvent,
} from '@react-native-community/datetimepicker';
import React, { useState } from 'react';
import {
  PaddleSpeed,
  RouteDifficulty,
  RouteType,
} from '../models/userInputModel';
import { SelectButtons, generateOptions } from './SelectionButtons';

export const Filters: React.FC<{
  startTime: Date;
  setStartTime: React.Dispatch<React.SetStateAction<Date>>;
  endTime: Date;
  setEndTime: React.Dispatch<React.SetStateAction<Date>>;
}> = ({ startTime, setStartTime, endTime, setEndTime }) => {
  const [showEndTimePicker, setShowEndTimePicker] = useState(false);

  const onStartTimeChange = (
    _event: DateTimePickerEvent,
    selectedStartTime: Date,
  ) => {
    const currentStart: Date = selectedStartTime || startTime;
    setStartTime(currentStart);
    setShowEndTimePicker(true);
  };

  const onEndTimeChange = (
    _event: DateTimePickerEvent,
    selectedEndTime: Date,
  ) => {
    const currentEnd: Date = selectedEndTime || endTime;
    setEndTime(currentEnd);
  };

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

  const [breakDuration, setBreakDuration] = useState(new Date(0));

  const onBreakDurationChange = (
    _event: DateTimePickerEvent,
    selectedBreakDuration: Date,
  ) => {
    const currentBreak: Date = selectedBreakDuration || breakDuration;
    setBreakDuration(currentBreak);
  };

  return (
    <View>
      <View style={styles.timePickerContainer}>
        <View style={styles.pickerContainer}>
          <Text style={styles.label}>Enter Start Time</Text>
          <DateTimePicker
            value={startTime}
            mode="time"
            is24Hour={true}
            minimumDate={new Date()}
            onChange={onStartTimeChange}
          ></DateTimePicker>
        </View>
        <View style={styles.pickerContainer}>
          <Text style={styles.label}>Enter End Time</Text>
          <DateTimePicker
            value={endTime}
            mode="time"
            is24Hour={true}
            minimumDate={startTime}
            disabled={!showEndTimePicker}
            onChange={onEndTimeChange}
          ></DateTimePicker>
        </View>
      </View>
      <SelectButtons
        label={'Select paddle speed'}
        options={paddleSpeedOptions}
        selectedOption={paddleSpeed}
        onSelect={setPaddleSpeed}
      />
      <View style={{ height: 20 }} />
      <SelectButtons
        label={'Select route difficulty'}
        options={routeDifficultyOptions}
        selectedOption={routeDifficulty}
        onSelect={setRouteDifficulty}
      />
      <View style={{ height: 20 }} />
      <SelectButtons
        label={'Select route type'}
        options={routeTypeOptions}
        selectedOption={routeType}
        onSelect={setRouteType}
      />
      <View style={{ height: 20 }} />
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

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
import { Button } from 'react-native-paper';
import { colors } from '../../colors';

type FiltersProps = {
  setUserInput: React.Dispatch<React.SetStateAction<UserInput>>;
  onFindRoutesPressed: () => void;
  weatherDates: Date[];
};

export const Filters: React.FC<FiltersProps> = ({
  setUserInput,
  onFindRoutesPressed,
  weatherDates,
}: FiltersProps) => {
  const [startTime, setStartTime] = useState<Date>(new Date());

  const zeroDuration = new Date(0, 0, 0, 0, 0, 0);
  const defaultDuration = new Date(0, 0, 0, 2, 0, 0);

  const [durationAsDate, setDurationAsDate] = useState<Date>(defaultDuration);
  const [paddleSpeed, setPaddleSpeed] = useState<PaddleSpeed>(
    PaddleSpeed.Normal,
  );
  const [routeType, setRouteType] = useState<RouteType>(
    RouteType['Point-to-point'],
  );
  const [routeDifficulty, setRouteDifficulty] = useState<RouteDifficulty>(
    RouteDifficulty.Any,
  );

  const paddleSpeedOptions = generateOptions(PaddleSpeed);
  const routeTypeOptions = generateOptions(RouteType);
  const routeDifficultyOptions = generateOptions(RouteDifficulty);

  useEffect(() => {
    setUserInput((prevUserInput) => ({
      ...prevUserInput,
      startTime: startTime,
      duration: durationAsDate.getMinutes() + durationAsDate.getHours() * 60,
      paddleSpeed: paddleSpeed,
      routeType: routeType,
      routeDifficulty: routeDifficulty,
    }));
  }, [startTime, durationAsDate, paddleSpeed, routeType, routeDifficulty]);

  return (
    <View style={styles.container}>
      <Button
        style={styles.findRoutesButton}
        mode="contained"
        onPress={onFindRoutesPressed}
      >
        <Text style={styles.title}>Find Routes</Text>
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
            minimumDate={weatherDates[0]}
            maximumDate={weatherDates[weatherDates.length - 1]}
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
            minimumDate={weatherDates[0]}
            maximumDate={weatherDates[weatherDates.length - 1]}
          />
        </View>
        <View style={styles.column}>
          <Text style={styles.title}>Duration</Text>
          <DateTimePicker
            value={durationAsDate}
            mode="time"
            display="default"
            onChange={(
              _event: DateTimePickerEvent,
              selectedStartTime: Date,
            ) => {
              if (_event.type === 'set') {
                setDurationAsDate(selectedStartTime);
              }
            }}
            maximumDate={
              weatherDates.length > 1
                ? new Date(
                    zeroDuration.getTime() +
                      weatherDates[weatherDates.length - 1].getTime() -
                      startTime.getTime(),
                  )
                : undefined
            }
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
        label={'Route Type'}
        options={routeTypeOptions}
        selectedOption={routeType}
        onSelect={setRouteType}
      />
      <View style={styles.divider}></View>
      <SelectButtons
        label={'Difficulty'}
        options={routeDifficultyOptions}
        selectedOption={routeDifficulty}
        onSelect={setRouteDifficulty}
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
  findRoutesButton: {
    backgroundColor: colors.orange.medium,
  },
});

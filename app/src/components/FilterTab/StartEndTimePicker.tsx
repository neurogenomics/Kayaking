import { StyleSheet, Text, View } from 'react-native';
import DateTimePicker, {
  DateTimePickerEvent,
} from '@react-native-community/datetimepicker';
import React, { useState } from 'react';

export const StartEndTimePicker: React.FC<{
  startTime: Date;
  setStartTime: React.Dispatch<React.SetStateAction<Date>>;
  endTime: Date;
  setEndTime: React.Dispatch<React.SetStateAction<Date>>;
}> = ({ startTime, setStartTime, endTime, setEndTime }) => {
  const onStartTimeChange = (
    _event: DateTimePickerEvent,
    selectedStartTime: Date,
  ) => {
    const currentStart: Date = selectedStartTime || startTime;
    setStartTime(currentStart);
  };

  const onEndTimeChange = (
    _event: DateTimePickerEvent,
    selectedEndTime: Date,
  ) => {
    const currentEnd: Date = selectedEndTime || endTime;
    setEndTime(currentEnd);
  };

  return (
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
          onChange={onEndTimeChange}
        ></DateTimePicker>
      </View>
    </View>
  );
};

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

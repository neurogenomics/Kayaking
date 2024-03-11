import {
  getDifficultyLabel,
  isCircular,
  RouteModel,
} from '../../models/routeModel';
import React from 'react';
import { Text, View, StyleSheet } from 'react-native';
import { Icon } from 'react-native-paper';

type RouteInfoRowProps = {
  route: RouteModel;
  timeDisplayStr: string;
  startTime: Date; // displayed for circular routes
};

const styles = StyleSheet.create({
  rowContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    width: '100%',
  },
  textContainer: {
    justifyContent: 'center',
    flex: 1,
    alignItems: 'center',
    flexDirection: 'row',
    textAlign: 'center',
  },
  difficultyTextContainer: {
    padding: 4,
    borderRadius: 4,
    backgroundColor: 'rgb(0, 200, 0, 0.3)',
  },
  text: {
    fontSize: 18,
    marginHorizontal: 3,
  },
});

export const RouteInfoRow: React.FC<RouteInfoRowProps> = ({
  route,
  timeDisplayStr,
  startTime,
}) => {
  return route !== undefined ? (
    <View style={styles.rowContainer}>
      {isCircular(route) ? (
        <View style={styles.textContainer}>
          <Icon source="clock-outline" size={24} />
          <Text style={styles.text}>
            {startTime.toLocaleTimeString([], {
              hour: '2-digit',
              minute: '2-digit',
            })}
          </Text>
        </View>
      ) : null}
      <View style={styles.textContainer}>
        <Icon source="kayaking" size={24} />
        <Text style={styles.text}>{(route.length / 1000).toFixed(1)}km</Text>
      </View>
      <View style={styles.textContainer}>
        <Icon source="timer" size={24} />
        <Text style={styles.text}>{timeDisplayStr}</Text>
      </View>
      <View style={styles.textContainer}>
        <Text style={styles.difficultyText}>
          {getDifficultyLabel(route.difficulty)}
        </Text>
      </View>
    </View>
  ) : null;
};

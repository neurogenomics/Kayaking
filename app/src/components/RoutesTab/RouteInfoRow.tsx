import { getDifficultyLabel, RouteModel } from '../../models/routeModel';
import React from 'react';
import { Text, View, StyleSheet } from 'react-native';
import { Icon } from 'react-native-paper';
import { difficultyColours } from '../../colors';

type RouteInfoRowProps = {
  route: RouteModel;
  timeDisplayStr: string;
  showTime: boolean;
};

const styles = StyleSheet.create({
  rowContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    width: '100%',
    margin: 4,
  },
  textContainer: {
    justifyContent: 'center',
    flex: 1,
    alignItems: 'center',
    flexDirection: 'row',
    textAlign: 'center',
  },
  text: {
    fontSize: 16,
    marginHorizontal: 3,
  },
});

export const RouteInfoRow: React.FC<RouteInfoRowProps> = ({
  route,
  timeDisplayStr,
  showTime,
}) => {
  return route !== undefined ? (
    <View style={styles.rowContainer}>
      {showTime ? (
        <View style={styles.textContainer}>
          <Icon source="clock-outline" size={24} />
          <Text style={styles.text}>
            {route.startTime.toLocaleTimeString([], {
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
        <View
          style={{
            padding: 4,
            borderRadius: 4,
            backgroundColor:
              difficultyColours[getDifficultyLabel(route.difficulty)],
          }}
        >
          <Text style={styles.text}>
            {getDifficultyLabel(route.difficulty)}
          </Text>
        </View>
      </View>
    </View>
  ) : null;
};

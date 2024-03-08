import { getDifficultyLabel, RouteModel } from '../../models/routeModel';
import React from 'react';
import { Text, View, StyleSheet } from 'react-native';
import { Icon } from 'react-native-paper';

type RouteInfoRowProps = {
  route: RouteModel;
  timeDisplayStr: string;
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
  text: {
    fontSize: 18,
    marginHorizontal: 3,
  },
});
export const RouteInfoRow: React.FC<RouteInfoRowProps> = ({
  route,
  timeDisplayStr,
}) => {
  return route !== undefined ? (
    <View style={styles.rowContainer}>
      <View style={styles.textContainer}>
        <Icon source="kayaking" size={24} />
        <Text style={styles.text}>{(route.length / 1000).toFixed(1)}km</Text>
      </View>
      <View style={styles.textContainer}>
        <Icon source="clock-time-eight-outline" size={24} />
        <Text style={styles.text}>{timeDisplayStr}</Text>
      </View>
      <View style={styles.textContainer}>
        <Text style={styles.text}>{getDifficultyLabel(route.difficulty)}</Text>
      </View>
    </View>
  ) : null;
};

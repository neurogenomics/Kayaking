import { StyleSheet, View, Text } from 'react-native';
import Speedometer, { Arc, Progress } from 'react-native-cool-speedometer';
import { RouteModel } from '../../models/routeModel';
import {
  getInterpolatedColor,
  speedMapColours,
} from '../../colors';
type RouteInformationProps = {
  route: RouteModel;
};

const styles = StyleSheet.create({
  label: {
    fontSize: 18,
    marginBottom: 5,
    marginTop: 0,
  },
  speedometer: {
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: -70,
  },
});

export const RouteSpeedometer: React.FC<RouteInformationProps> = ({
  route,
}: RouteInformationProps) => {
  
  return (
    <View>
      <Text style={styles.label}>Difficulty: {route.difficulty} / 12</Text>
      <View style={styles.speedometer}>
        <Speedometer
          value={route.difficulty}
          max={10}
          angle={180}
          lineCap="round"
          accentColor={getInterpolatedColor(
            route.difficulty,
            [1, 12],
            speedMapColours,
          )}
        >
          <Arc arcWidth={40} />
          <Progress arcWidth={40} />
        </Speedometer>
      </View>
    </View>
  );
};
export default RouteSpeedometer;

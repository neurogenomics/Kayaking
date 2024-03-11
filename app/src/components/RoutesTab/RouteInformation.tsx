import { StyleSheet, View, Text, Dimensions } from 'react-native';
import { LineChart } from 'react-native-chart-kit';
import Speedometer, { Arc, Progress } from 'react-native-cool-speedometer';
import { RouteModel, getRouteSpeeds } from '../../models/routeModel';
import { getWindsDirection } from '../../services/windService';
import { useEffect, useState } from 'react';
import { Vector } from '../../models/vectorModel';
import {
  angleBetweenLocations,
  calculateDistanceBetweenLocations,
  toRadians,
} from '../../models/locationModel';
import {
  colors,
  fabColors,
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
  graphContainer: {
    width: '100%',
    height: 200,
    marginVertical: 10,
    borderRadius: 10,
    overflow: 'hidden',
  },
});

export const RouteInformation: React.FC<RouteInformationProps> = ({
  route,
}: RouteInformationProps) => {
  const [windsInfo, setWindsInfo] = useState<number[]>([0]);

  useEffect(() => {
    getWindsDirection(route.locations, route.checkpoints, route.startTime)
      .then(() => {})
      .catch((err) => console.error(err));
    void getWindsDirection(
      route.locations,
      route.checkpoints,
      route.startTime,
    ).then((winds) => {
      //vectors of speed kayaker is going at
      const speedVectors: Vector[] = [];
      for (let i = 0; i < route.locations.length - 1; i++) {
        const loc1 = route.locations[i];
        const loc2 = route.locations[i + 1];
        const time = route.checkpoints[i + 1] - route.checkpoints[i]; // Time taken to travel from loc1 to loc2

        const distance = calculateDistanceBetweenLocations(loc1, loc2);
        const angle = toRadians(angleBetweenLocations(loc1, loc2));

        const displacement = {
          u: distance * Math.cos(angle), // Assuming lat represents u component
          v: distance * Math.sin(angle), // Assuming long represents v component
        };
        if (time === 0) {
          speedVectors.push(i === 0 ? { u: 0, v: 0 } : speedVectors[i - 1]);
        } else {
          speedVectors.push({
            u: displacement.u / time,
            v: displacement.v / time,
          });
        }
      }
      const windScalar: number[] = speedVectors.map((vel, index) => {
        //wind projected onto velocity
        const w_dot_v = vel.u * winds[index].u + winds[index].v * vel.v;
        const v_dot_v = vel.u * vel.u + vel.v * vel.v;
        return parseFloat((w_dot_v / v_dot_v).toFixed(2));
      });
      if (windScalar.length !== 0) {
        setWindsInfo(windScalar);
      }
    });
  }, [route]);

  const halfHours = Math.floor(
    route.checkpoints[route.checkpoints.length - 1] / 1800,
  );
  const times: string[] = ['0:00'];
  for (let i = 0; i < halfHours; i++) {
    times.push(`${Math.floor((i + 1) / 2)}:${i % 2 !== 0 ? '00' : '30'}`); // 0:00, 0:30, 1:00, etc
  }

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
      <Text style={styles.label}>Paddling speed</Text>
      <View style={styles.graphContainer}>
        <LineChart
          data={{
            labels: times,
            datasets: [
              {
                data: getRouteSpeeds(route),
              },
            ],
          }}
          width={Dimensions.get('window').width}
          height={200}
          yAxisLabel=""
          yAxisSuffix="m/s"
          yAxisInterval={4}
          fromZero={true}
          chartConfig={{
            backgroundColor: colors.orange.medium,
            backgroundGradientFrom: colors.orange.medium,
            backgroundGradientTo: colors.orange.medium,
            fillShadowGradientFromOpacity: 0,
            fillShadowGradientToOpacity: 0,
            decimalPlaces: 0,
            color: (opacity = 1) => `rgba(255, 255, 255, ${opacity})`,
            labelColor: (opacity = 1) => `rgba(255, 255, 255, ${opacity})`,
            style: {
              borderRadius: 0,
            },
            propsForDots: {
              r: '0',
              strokeWidth: '2',
              stroke: '#CC99FF',
            },
          }}
          style={{
            marginVertical: 0,
            borderRadius: 0,
          }}
        />
      </View>

      <Text style={styles.label}>Wind support</Text>
      <View style={styles.graphContainer}>
        <LineChart
          data={{
            labels: times,
            datasets: [
              {
                data: windsInfo.length === 0 ? [0] : windsInfo,
              },
            ],
          }}
          width={Dimensions.get('window').width}
          height={200}
          yAxisLabel=""
          yAxisSuffix="m/s"
          yAxisInterval={4}
          chartConfig={{
            backgroundColor: colors.orange.medium,
            backgroundGradientFrom: colors.orange.medium,
            backgroundGradientTo: colors.orange.medium,
            fillShadowGradientFromOpacity: 0,
            fillShadowGradientToOpacity: 0,
            decimalPlaces: 0,
            color: (opacity = 1) => `rgba(255, 255, 255, ${opacity})`,
            labelColor: (opacity = 1) => `rgba(255, 255, 255, ${opacity})`,
            style: {
              borderRadius: 0,
            },
            propsForDots: {
              r: '0',
              strokeWidth: '2',
              stroke: fabColors.fabSelected,
            },
          }}
          style={{
            marginVertical: 0,
            borderRadius: 0,
          }}
        />
      </View>
    </View>
  );
};
export default RouteInformation;

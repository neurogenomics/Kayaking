import { StyleSheet, View, Text } from 'react-native';
import {
  LineChart,
  BarChart,
  PieChart,
  ProgressChart,
  ContributionGraph,
  StackedBarChart,
} from 'react-native-chart-kit';
import Speedometer, {
  Background,
  Arc,
  Needle,
  Progress,
  Marks,
  Indicator,
} from 'react-native-cool-speedometer';
import { RouteModel, getRouteSpeeds } from '../models/routeModel';
import { ScrollView } from 'react-native-gesture-handler';
import { getWindsDirection } from '../services/windService';
import { useEffect, useState } from 'react';
import { Vector } from '../models/vectorModel';
import {
  angleBetweenLocations,
  calculateDistanceBetweenLocations,
  toRadians,
} from '../models/locationModel';
type RouteInformationProps = {
  route: RouteModel;
};

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
        return (w_dot_v / v_dot_v).toFixed(2);
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
    times.push(`${Math.floor((i + 1)/2)}:${i % 2 !== 0 ? '00' : '30'}`); // Assuming you want the hour numbers starting from 1
  }

  console.log('hey these are the times');
  console.log(times);

  const center = 250 / 2;

  return (
    <View>
      <Text style={styles.label}>Paddling speed</Text>
      <LineChart
        data={{
          labels: times,
          datasets: [
            {
              data: getRouteSpeeds(route),
            },
          ],
        }}
        width={400} //{Dimensions.get('window').width} // from react-native
        height={200}
        yAxisLabel=""
        yAxisSuffix="m/s"
        yAxisInterval={4} // optional, defaults to 1
        fromZero={true}
        chartConfig={{
          backgroundColor: '#CC99FF',
          backgroundGradientFrom: '#CC99FF',
          backgroundGradientTo: '#CC99FF',
          fillShadowGradientFromOpacity: 0,
          fillShadowGradientToOpacity: 0,
          decimalPlaces: 0, // optional, defaults to 2dp
          color: (opacity = 1) => `rgba(255, 255, 255, ${opacity})`,
          labelColor: (opacity = 1) => `rgba(255, 255, 255, ${opacity})`,
          style: {
            borderRadius: 16,
          },
          propsForDots: {
            r: '0',
            strokeWidth: '2',
            stroke: '#CC99FF',
          },
        }}
        style={{
          marginVertical: 8,
          borderRadius: 16,
        }}
      />
      <Text style={styles.label}>Wind Support</Text>
      <LineChart
        data={{
          labels: times,
          datasets: [
            {
              data: windsInfo.length === 0 ? [0] : windsInfo,
            },
          ],
        }}
        width={400} //{Dimensions.get('window').width} // from react-native
        height={200}
        yAxisLabel=""
        yAxisSuffix="m/s"
        yAxisInterval={4} // optional, defaults to 1
        chartConfig={{
          backgroundColor: '#CC99FF',
          backgroundGradientFrom: '#CC99FF',
          backgroundGradientTo: '#CC99FF',
          fillShadowGradientFromOpacity: 0,
          fillShadowGradientToOpacity: 0,
          decimalPlaces: 0, // optional, defaults to 2dp
          color: (opacity = 1) => `rgba(255, 255, 255, ${opacity})`,
          labelColor: (opacity = 1) => `rgba(255, 255, 255, ${opacity})`,
          style: {
            borderRadius: 16,
          },
          propsForDots: {
            r: '0',
            strokeWidth: '2',
            stroke: '#CC99FF',
          },
        }}
        style={{
          marginVertical: 8,
          borderRadius: 16,
        }}
      />
      <Text style={styles.label}>Difficulty</Text>
      <Speedometer
        value={route.difficulty}
        max={10}
        angle={180}
        lineCap="round"
        accentColor="rgb(204, 153, 255)"
      >
        <Arc arcWidth={40} />
        <Progress arcWidth={40} />
        <Indicator fixValue={false}>
          {(value, textProps) => (
            <Text
              {...textProps}
              //fontSize={60}
              //fill="orange"
              //x={center}
              //y={center + 10}
              //textAnchor="middle"
              //alignmentBaseline="middle"
            >
              {value}/10
            </Text>
          )}
        </Indicator>
      </Speedometer>
    </View>
  );
};
export default RouteInformation;

const styles = StyleSheet.create({
  label: {
    fontSize: 18,
    marginBottom: 5,
    margin: 10,
  },
});

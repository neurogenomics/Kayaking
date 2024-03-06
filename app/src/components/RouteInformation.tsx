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
import { RouteModel } from '../models/routeModel';
import { ScrollView } from 'react-native-gesture-handler';
import { getWindsDirection } from '../services/windService';
import { useEffect, useState } from 'react';
import { Vector } from '../models/vectorModel';
type RouteInformationProps = {
  route: RouteModel;
};

export const RouteInformation: React.FC<RouteInformationProps> = ({
  route,
}: RouteInformationProps) => {
  const times: string[] = [];
  const currentDate = new Date(route.startTime);

  const [windsInfo, setWindsInfo] = useState<number[]>([]);

  /* useEffect(() => {
    void getWindsDirection(
      route.locations,
      route.checkpoints,
      route.startTime,
    ).then((winds) => {
      const speedVectors: Vector[] = [];
      for (let i = 0; i < route.locations.length - 1; i++) {
        const loc1 = route.locations[i];
        const loc2 = route.locations[i + 1];
        const time = route.checkpoints[i + 1] - route.checkpoints[i]; // Time taken to travel from loc1 to loc2

        const displacement = {
          u: loc2.latitude - loc1.latitude, // Assuming lat represents u component
          v: loc2.longitude - loc1.longitude, // Assuming long represents v component
        };

        const speed = {
          u: displacement.u / time,
          v: displacement.v / time,
        };

        speedVectors.push(speed);
      }

      const windScalar: number[] = speedVectors.map((vel, index) => {
        const velMagnitude = Math.sqrt(vel.u ** 2 + vel.v ** 2);
        const dotProd = vel.u * vel.u + winds[index].v * winds[index].v;
        return dotProd / velMagnitude;
      });

      setWindsInfo(windScalar);
    });
  }, []); */

  while (currentDate <= route.endTime) {
    const hours = currentDate.getHours().toString().padStart(2, '0');
    const minutes = currentDate.getMinutes().toString().padStart(2, '0');
    times.push(`${hours}:${minutes}`);
    currentDate.setMinutes(currentDate.getMinutes() + 60);
  }

  const center = 250 / 2;

  return (
    <ScrollView>
      <Text style={styles.label}>Estimated Speed</Text>
      <Text> {times.join(', ')} </Text>
      <LineChart
        data={{
          labels: times,
          datasets: [
            {
              data: Array.from({ length: 30 }, () =>
                Math.floor(Math.random() * 50),
              ),
            },
          ],
        }}
        width={380} //{Dimensions.get('window').width} // from react-native
        height={250}
        yAxisLabel=" "
        yAxisSuffix="m/s"
        yAxisInterval={5} // optional, defaults to 1
        fromZero={true}
        chartConfig={{
          backgroundColor: '#CC99FF',
          backgroundGradientFrom: '#CC99FF',
          backgroundGradientTo: '#CC99FF',
          decimalPlaces: 2, // optional, defaults to 2dp
          color: (opacity = 1) => `rgba(255, 255, 255, ${opacity})`,
          labelColor: (opacity = 1) => `rgba(255, 255, 255, ${opacity})`,
          style: {
            borderRadius: 16,
          },
          propsForDots: {
            r: '0',
            strokeWidth: '2',
            stroke: '#ffa726',
          },
        }}
        bezier
        style={{
          marginVertical: 8,
          borderRadius: 10,
        }}
      />
      <Text style={styles.label}>Support winds</Text>
      <LineChart
        data={{
          labels: ['January', 'February', 'March', 'April', 'May', 'June'],
          datasets: [
            {
              data: windsInfo,
            },
            {
              data: [0, 0, 0, 0, 0, 0],
            },
          ],
        }}
        width={400} //{Dimensions.get('window').width} // from react-native
        height={200}
        yAxisLabel=""
        yAxisSuffix="m/s"
        yAxisInterval={1} // optional, defaults to 1
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
    </ScrollView>
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

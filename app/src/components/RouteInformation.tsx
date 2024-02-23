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
import { Route } from '../models/routeModel';
import { ScrollView } from 'react-native-gesture-handler';
const RouteInformation = (route: Route) => {
  const times: string[] = [];
  const currentDate = new Date(route.startTime);

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
          backgroundColor: '#e26a00',
          backgroundGradientFrom: '#fb8c00',
          backgroundGradientTo: '#ffa726',
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
      <Text style={styles.label}>Estimated Speed</Text>
      <LineChart
        data={{
          labels: ['January', 'February', 'March', 'April', 'May', 'June'],
          datasets: [
            {
              data: [10, 20, 30, 40, 50],
            },
          ],
        }}
        width={400} //{Dimensions.get('window').width} // from react-native
        height={150}
        yAxisLabel="$"
        yAxisSuffix="k"
        yAxisInterval={1} // optional, defaults to 1
        chartConfig={{
          backgroundColor: '#e26a00',
          backgroundGradientFrom: '#fb8c00',
          backgroundGradientTo: '#ffa726',
          decimalPlaces: 2, // optional, defaults to 2dp
          color: (opacity = 1) => `rgba(255, 255, 255, ${opacity})`,
          labelColor: (opacity = 1) => `rgba(255, 255, 255, ${opacity})`,
          style: {
            borderRadius: 16,
          },
          propsForDots: {
            r: '6',
            strokeWidth: '2',
            stroke: '#ffa726',
          },
        }}
        bezier
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
        accentColor="orange"
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
              {value}%
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

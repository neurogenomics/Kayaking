import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { RootStackParamList, Route } from '../routes';
import React, { useMemo, useState } from 'react';
import { StyleSheet, SafeAreaView } from 'react-native';
import MapView from 'react-native-maps';
import { isleOfWight } from '../../constants';
import BottomSheet from '@gorhom/bottom-sheet';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { createMaterialTopTabNavigator } from '@react-navigation/material-top-tabs';
import Routes from '../components/Routes';
import Filters from '../components/Filter';
import Animated, {
  useAnimatedStyle,
  useSharedValue,
} from 'react-native-reanimated';
import WeatherFabs from '../components/WeatherFabs';
import { MapVisualisation } from '../components/MapVisualisation';
import DateCarousel from '../components/DateCarousel/DateCarousel';
import { GridType } from '../models/gridModel';
import { DataDisplay } from '../components/DataDisplay';

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  map: StyleSheet.absoluteFillObject,
  carouselContainer: {
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
    borderRadius: 12,
  },
});

type HomeProps = NativeStackScreenProps<RootStackParamList, Route.HOME>;
const HomeScreen: React.FC<HomeProps> = () => {
  const [fabsVisible, setFabsVisible] = useState(true);
  const [weatherMap, setWeatherMap] = useState<GridType>();
  const [sunsetOn, setSunsetOn] = useState(false);
  const [tideHeightOn, setTideTimesOn] = useState(false);
  const snapPoints = useMemo(() => ['15%', '50%', '90%'], []);
  const [mapDate, setMapDate] = useState<Date>(new Date());
  const bottomSheetPosition = useSharedValue<number>(0);
  const Tab = createMaterialTopTabNavigator();

  const inverseBottomSheetStyle = useAnimatedStyle(() => {
    return {
      position: 'absolute',
      top: 0,
      right: 0,
      width: '100%',
      height: bottomSheetPosition.value,
    };
  });

  // TODO: This should call the server to find what times the weather data is available but no such route exists yet
  const getNextHours = () => {
    const result: Date[] = [];
    const startTime = new Date();
    startTime.setMinutes(0);
    startTime.setSeconds(0);
    startTime.setMilliseconds(0);
    for (let i = 0; i <= 50; i++) {
      const nextHour = new Date(startTime.getTime() + i * 3600 * 1000);
      result.push(nextHour);
    }
    return result;
  };

  const [startTime, setStartTime] = useState(new Date());
  const [endTime, setEndTime] = useState(new Date());

  return (
    <GestureHandlerRootView style={styles.container}>
      <MapView
        style={styles.map}
        initialRegion={isleOfWight}
        rotateEnabled={true}
        scrollEnabled={true}
        provider="google"
      >
        {weatherMap !== undefined ? (
          <MapVisualisation display={weatherMap} date={mapDate} />
        ) : (
          <></>
        )}
      </MapView>
      <SafeAreaView style={styles.carouselContainer}>
        <DateCarousel
          dates={getNextHours()}
          onDateChanged={(date) => setMapDate(date)}
        ></DateCarousel>
      </SafeAreaView>
      <DataDisplay
        sunsetOn={sunsetOn}
        tideTimesOn={tideHeightOn}
        // TODO get from map?
        location={{
          longitude: isleOfWight.longitude,
          latitude: isleOfWight.latitude,
        }}
        date={mapDate}
      />
      {/*<Text style={{ fontSize: 100 }}>put here</Text>*/}
      <Animated.View style={inverseBottomSheetStyle} pointerEvents="box-none">
        <WeatherFabs
          visible={fabsVisible}
          setWeatherMap={setWeatherMap}
          setSunsetOn={setSunsetOn}
          setTideTimesOn={setTideTimesOn}
        ></WeatherFabs>
      </Animated.View>
      <BottomSheet
        index={0}
        snapPoints={snapPoints}
        animatedPosition={bottomSheetPosition}
        onChange={(num) => {
          //Hide fabs when the sheet is at the top of the screen
          setFabsVisible(num !== 2);
        }}
      >
        <Tab.Navigator>
          <Tab.Screen name="Filter" options={{ tabBarLabel: 'Filter' }}>
            {() => (
              <Filters
                startTime={startTime}
                setStartTime={setStartTime}
                endTime={endTime}
                setEndTime={setEndTime}
              />
            )}
          </Tab.Screen>
          <Tab.Screen name="Routes" component={Routes} />
        </Tab.Navigator>
      </BottomSheet>
    </GestureHandlerRootView>
  );
};

export default HomeScreen;

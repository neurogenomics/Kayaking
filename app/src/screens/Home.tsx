import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { RootStackParamList, Route } from '../routes';
import React, { useMemo, useState } from 'react';
import { SafeAreaView, StyleSheet } from 'react-native';
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
import { GridType } from '../models/gridModel';
import DateCarousel from '../components/DateCarousel/DateCarousel';
import { PaddleSpeed, RouteType, UserInput } from '../models/userInputModel';
import { RouteVisualisation } from '../components/RouteVisualisation';

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
  const snapPoints = useMemo(() => ['15%', '50%', '90%'], []);
  const [mapDate, setMapDate] = useState<Date>(new Date());
  const bottomSheetPosition = useSharedValue<number>(0);
  const Tab = createMaterialTopTabNavigator();
  const [userInput, setUserInput] = useState<UserInput>();

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

  // TODO: get rid of this once filter merged
  const start = new Date();
  start.setHours(13, 0, 0, 0);

  const end = new Date();
  end.setHours(15, 0, 0, 0);

  const input: UserInput = {
    startTime: start,
    endTime: end,
    location: {
      latitude: isleOfWight.latitude,
      longitude: isleOfWight.longitude,
    },
    paddleSpeed: PaddleSpeed.Normal,
    breakTime: 0,
    routeType: RouteType.PointToPoint,
  };
  setUserInput(input);

  return (
    <GestureHandlerRootView style={styles.container}>
      <MapView
        style={styles.map}
        initialRegion={isleOfWight}
        rotateEnabled={true}
        scrollEnabled={true}
        provider="google"
      >
        {/*{weatherMap !== undefined ? (*/}
        {/*  <WeatherVisualisation display={weatherMap} date={mapDate} />*/}
        {/*) : (*/}
        {/*  <></>*/}
        {/*)}*/}
        {userInput !== undefined ? (
          <RouteVisualisation userInput={userInput} />
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
      <Animated.View style={inverseBottomSheetStyle} pointerEvents="box-none">
        <WeatherFabs
          visible={fabsVisible}
          setWeatherMap={setWeatherMap}
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
          <Tab.Screen name="Filter" component={Filters} />
          <Tab.Screen name="Routes" component={Routes} />
        </Tab.Navigator>
      </BottomSheet>
    </GestureHandlerRootView>
  );
};

export default HomeScreen;

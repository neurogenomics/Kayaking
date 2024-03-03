import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { RootStackParamList } from '../routes';
import React, { useMemo, useState } from 'react';
import { SafeAreaView, StyleSheet } from 'react-native';
import MapView from 'react-native-maps';
import { isleOfWight } from '../../constants';
import BottomSheet from '@gorhom/bottom-sheet';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { createMaterialTopTabNavigator } from '@react-navigation/material-top-tabs';
import Routes from '../components/RoutesTab/Routes';
import Filters from '../components/FilterTab/Filters';
import Animated, {
  useAnimatedStyle,
  useSharedValue,
} from 'react-native-reanimated';
import WeatherFabs from '../components/WeatherFabs';
import { GridType } from '../models/gridModel';
import DateCarousel from '../components/DateCarousel/DateCarousel';
import { UserInput } from '../models/userInputModel';
import { WeatherVisualisation } from '../components/MapVisualisations/WeatherVisualisation';
import { RouteVisualisation } from '../components/MapVisualisations/RouteVisualisation';
import { RouteModel } from '../models/routeModel';
import { useNavigation } from '@react-navigation/native';
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
  const [userInput, setUserInput] = useState<UserInput>();
  const [routes, setRoutes] = useState<RouteModel[] | undefined>();
  const [selectedRouteIndex, setSelectedRouteIndex] = useState(0);

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
          <WeatherVisualisation display={weatherMap} date={mapDate} />
        ) : null}
        {userInput !== undefined ? (
          <RouteVisualisation
            userInput={userInput}
            routes={routes}
            setRoutes={setRoutes}
            selectedRouteIndex={selectedRouteIndex}
            setSelectedRouteIndex={setSelectedRouteIndex}
          />
        ) : null}
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
          <Tab.Screen name="Filter">
            {() => <Filters setUserInput={setUserInput} />}
          </Tab.Screen>
          <Tab.Screen name="Routes">
            {() => (
              <Routes
                routes={routes}
                selectedRouteIndex={selectedRouteIndex}
                setSelectedRouteIndex={setSelectedRouteIndex}
                navigation={useNavigation()}
              />
            )}
          </Tab.Screen>
        </Tab.Navigator>
      </BottomSheet>
    </GestureHandlerRootView>
  );
};

export default HomeScreen;

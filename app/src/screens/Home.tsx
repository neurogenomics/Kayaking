import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { RootStackParamList, Route } from '../routes';
import React, { useEffect, useMemo, useState } from 'react';
import { SafeAreaView, StyleSheet, View } from 'react-native';
import MapView, { Region } from 'react-native-maps';
import { isleOfWight } from '../../constants';
import BottomSheet, { BottomSheetScrollView } from '@gorhom/bottom-sheet';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { createMaterialTopTabNavigator } from '@react-navigation/material-top-tabs';
import Routes from '../components/RoutesTab/Routes';
import Filters from '../components/FilterTab/Filters';
import Animated, {
  useAnimatedStyle,
  useSharedValue,
} from 'react-native-reanimated';
import WeatherFabs from '../components/WeatherFabs';
import { WeatherGridType } from '../models/weatherGridModel';
import DateCarousel from '../components/DateCarousel/DateCarousel';
import { UserInput } from '../models/userInputModel';
import { WeatherVisualisation } from '../components/MapVisualisations/WeatherVisualisation';
import { RouteVisualisation } from '../components/MapVisualisations/RouteVisualisation';
import { RouteModel } from '../models/routeModel';
import { useNavigation } from '@react-navigation/native';
import { DataDisplay } from '../components/MapVisualisations/DataDisplay';
import { getWeatherDates } from '../services/timeService';
import SearchFab from '../components/SearchFab';
import { getRoute } from '../services/routeService';
import { WaveHeightVisualisation } from '../components/MapVisualisations/WaveHeightVisualisation';
import { TideInfo } from '../models/tideModel';
import { getTideTimes } from '../services/tideTimesService';
import { LocationModel } from '../models/locationModel';

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  map: StyleSheet.absoluteFillObject,
  carouselContainer: {
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
    borderRadius: 12,
  },
  searchFabContainer: {
    flex: 1,
    pointerEvents: 'box-none',
  },
});

type HomeProps = NativeStackScreenProps<RootStackParamList, Route.HOME>;
const HomeScreen: React.FC<HomeProps> = () => {
  const [fabsVisible, setFabsVisible] = useState(true);
  const [weatherMap, setWeatherMap] = useState<WeatherGridType>();
  const [sunsetOn, setSunsetOn] = useState(false);
  const [tideTimesOn, setTideTimesOn] = useState(false);
  const [waveHeightOn, setWaveHeightOn] = useState(false);
  const snapPoints = useMemo(() => ['15%', '50%', '90%'], []);
  const [mapDate, setMapDate] = useState<Date>(new Date());
  const bottomSheetPosition = useSharedValue<number>(0);
  const Tab = createMaterialTopTabNavigator();
  const [userInput, setUserInput] = useState<UserInput>();
  const [routes, setRoutes] = useState<RouteModel[]>([]);
  const [selectedRouteIndex, setSelectedRouteIndex] = useState(0);
  const [weatherDates, setWeatherDates] = useState<Date[]>([]);
  const [region, setRegion] = useState<Region>(isleOfWight);

  const [tideInfo, setTideInfo] = useState<TideInfo>();

  useEffect(() => {
    if (tideTimesOn) {
      const center: LocationModel = {
        latitude: region.latitude,
        longitude: region.longitude,
      };
      void getTideTimes(center).then((tideTimes) => {
        setTideInfo(tideTimes);
      });
    }
  }, [tideTimesOn, region]);

  const [isSearching, setIsSearching] = useState(false);

  const handleSearch = () => {
    if (isSearching) {
      return;
    }
    setIsSearching(true);
    void searchRoutes().finally(() => setIsSearching(false));
  };
  const inverseBottomSheetStyle = useAnimatedStyle(() => {
    return {
      position: 'absolute',
      top: 0,
      right: 0,
      width: '100%',
      height: bottomSheetPosition.value,
    };
  });

  function indexOfClosestPastDate(dates: Date[]): number | null {
    if (dates.length === 0) return null;

    const currentDate = new Date();
    const pastDates = dates.filter(
      (date) => date.getTime() <= currentDate.getTime(),
    );
    if (pastDates.length === 0) return null;

    let minDifference = Math.abs(
      pastDates[0].getTime() - currentDate.getTime(),
    );
    let closestIndex = 0;

    for (let i = 1; i < pastDates.length; i++) {
      const difference = Math.abs(
        pastDates[i].getTime() - currentDate.getTime(),
      );
      if (difference < minDifference) {
        minDifference = difference;
        closestIndex = i;
      }
    }
    return dates.indexOf(pastDates[closestIndex]);
  }

  const getWeatherMap = (weatherMap: WeatherGridType | undefined) => {
    if (
      weatherMap === WeatherGridType.WIND ||
      weatherMap === WeatherGridType.TIDE
    ) {
      return <WeatherVisualisation display={weatherMap} date={mapDate} />;
    }
    return null;
  };

  useEffect(() => {
    void getWeatherDates().then((dates) => {
      setWeatherDates(dates);
    });
  }, []);

  const searchRoutes = async () => {
    if (userInput) {
      setRoutes(await getRoute(region, userInput));
    }
  };

  return (
    <GestureHandlerRootView style={styles.container}>
      <MapView
        style={styles.map}
        initialRegion={region}
        rotateEnabled={true}
        scrollEnabled={true}
        provider="google"
        onRegionChangeComplete={setRegion}
      >
        {getWeatherMap(weatherMap)}
        {waveHeightOn ? <WaveHeightVisualisation date={mapDate} /> : null}
        {userInput !== undefined ? (
          <RouteVisualisation
            userInput={userInput}
            routes={routes}
            selectedRouteIndex={selectedRouteIndex}
            setSelectedRouteIndex={setSelectedRouteIndex}
            showWindWarnings={weatherMap === WeatherGridType.WIND}
          />
        ) : null}
      </MapView>
      <SafeAreaView style={styles.carouselContainer}>
        <DateCarousel
          dates={weatherDates}
          defaultIndex={indexOfClosestPastDate(weatherDates) ?? 0}
          onDateChanged={(date) => setMapDate(date)}
        ></DateCarousel>
      </SafeAreaView>
      <DataDisplay
        sunsetOn={sunsetOn}
        tideTimesOn={tideTimesOn}
        tideInfo={tideInfo}
        startTime={mapDate}
        location={isleOfWight}
      />
      <View style={styles.searchFabContainer}>
        <SearchFab
          onSearch={handleSearch}
          isSearching={isSearching}
        ></SearchFab>
      </View>
      <Animated.View style={inverseBottomSheetStyle} pointerEvents="box-none">
        <WeatherFabs
          visible={fabsVisible}
          setWeatherMap={setWeatherMap}
          setSunsetOn={setSunsetOn}
          setWaveHeightOn={setWaveHeightOn}
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
            {() => (
              <BottomSheetScrollView style={{ backgroundColor: 'white' }}>
                <Filters
                  setUserInput={setUserInput}
                  onFindRoutesPressed={handleSearch}
                  weatherDates={weatherDates}
                />
              </BottomSheetScrollView>
            )}
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

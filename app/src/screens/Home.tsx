import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { RootStackParamList, Route } from '../routes';
import React, { useMemo, useState } from 'react';
import { StyleSheet } from 'react-native';
import MapView from 'react-native-maps';
import { isleOfWight } from '../../constants';
import BottomSheet from '@gorhom/bottom-sheet';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { createMaterialTopTabNavigator } from '@react-navigation/material-top-tabs';
import Routes from '../components/Routes';
import Filters from '../components/Filter';
import { FAB } from 'react-native-paper';
import Animated, {
  useAnimatedStyle,
  useSharedValue,
} from 'react-native-reanimated';
import { COLORS } from '../colors';
import { IconSource } from 'react-native-paper/lib/typescript/components/Icon';

const styles = StyleSheet.create({
  container: {
    flex: 1,
    height: '100%',
    width: '100%',
    alignItems: 'center',
    justifyContent: 'center',
  },
  container2: {
    height: '100%',
    width: '100%',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: 'red',
  },
  map: {
    flex: 1,
    height: '50%',
    width: '100%',
    alignItems: 'center',
    justifyContent: 'center',
  },
  fabGroup: {
    position: 'absolute',
    right: 0,
    bottom: 0, // Adjust as needed
  },
  fab: {
    backgroundColor: COLORS.fabUnselected,
  },
});

type HomeProps = NativeStackScreenProps<RootStackParamList, Route.HOME>;
const HomeScreen: React.FC<HomeProps> = () => {
  const snapPoints = useMemo(() => ['15%', '50%', '90%'], []);

  const [layersOpen, setLayersOpen] = useState(false);
  const [fabsVisible, setFabsVisible] = useState(true);

  //   const [layers, setLayers] = useState({
  //     wind: false,
  //     tide: false,
  //     waveHeight: false,
  //     sunset: false,
  //   });

  const icons = ['weather-sunset', 'waves-arrow-up'];

  const [layers, setLayers] = useState([false, false]);

  const actions = [
    { icon: 'weather-sunset', property: 'sunset' },
    { icon: 'waves-arrow-up', property: 'tide' },
    { icon: 'waves-arrow-right', property: 'waveHeight' },
    { icon: 'weather-windy', property: 'wind' },
  ];

  const Tab = createMaterialTopTabNavigator();
  const bottomSheetPosition = useSharedValue<number>(0);

  const viewTextStyle = useAnimatedStyle(() => {
    return {
      position: 'absolute',
      top: 0,
      right: 0,
      width: '100%',
      height: bottomSheetPosition.value + 30,
    };
  });

  const handleActionPress = (index: number) => {
    const property = actions[index].property;
    if (property) {
      setLayers((prevState) => ({
        ...prevState,
        [property]: !prevState[property],
      }));
    }
  };

  return (
    <GestureHandlerRootView style={styles.container}>
      <MapView
        style={styles.map}
        initialRegion={isleOfWight}
        rotateEnabled={false}
      ></MapView>
      <Animated.View style={viewTextStyle} pointerEvents="box-none">
        <FAB.Group
          open={layersOpen && fabsVisible}
          visible={fabsVisible}
          style={styles.fabGroup}
          fabStyle={styles.fab}
          backdropColor={'transparent'}
          icon="layers"
          actions={actions.map((action, index) => ({
            icon: action.icon,
            onPress: () => handleActionPress(index),
            style: {
              backgroundColor: layers[action.property]
                ? COLORS.fabSelected
                : COLORS.fabUnselected,
            },
          }))}
          onPress={() => setLayersOpen(!layersOpen)}
          onStateChange={() => {}}
        />
      </Animated.View>
      <BottomSheet
        index={1}
        snapPoints={snapPoints}
        animatedPosition={bottomSheetPosition}
        onChange={(num) => {
          if (num === 2) {
            setFabsVisible(false);
          } else {
            setFabsVisible(true);
          }
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

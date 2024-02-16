import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { RootStackParamList, Route } from '../routes';
import React, { useMemo } from 'react';
import { StyleSheet } from 'react-native';
import MapView from 'react-native-maps';
import { isleOfWight } from '../../constants';
import BottomSheet from '@gorhom/bottom-sheet';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { createMaterialTopTabNavigator } from '@react-navigation/material-top-tabs';
import Routes from '../components/Routes';
import Filters from '../components/Filter';

const styles = StyleSheet.create({
  container: {
    flex: 1,
    height: '100%',
    width: '100%',
    alignItems: 'center',
    justifyContent: 'center',
  },
  map: {
    flex: 1,
    height: '50%',
    width: '100%',
    alignItems: 'center',
    justifyContent: 'center',
  },
});

type HomeProps = NativeStackScreenProps<RootStackParamList, Route.HOME>;
const HomeScreen: React.FC<HomeProps> = () => {
  const snapPoints = useMemo(() => ['15%', '50%', '90%'], []);

  const Tab = createMaterialTopTabNavigator();
  return (
    <GestureHandlerRootView style={styles.container}>
      <MapView
        style={styles.map}
        initialRegion={isleOfWight}
        rotateEnabled={false}
      ></MapView>
      <BottomSheet index={1} snapPoints={snapPoints}>
        <Tab.Navigator>
          <Tab.Screen name="Filter" component={Filters} />
          <Tab.Screen name="Routes" component={Routes} />
        </Tab.Navigator>
      </BottomSheet>
    </GestureHandlerRootView>
  );
};

export default HomeScreen;

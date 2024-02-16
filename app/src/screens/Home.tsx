import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { RootStackParamList, Route } from '../routes';
import React, { useMemo, useState } from 'react';
import { StyleSheet, Text } from 'react-native';
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
  fab: {
    position: 'absolute',
    margin: 16,
    right: 0,
    bottom: 200, // Adjust as needed
  },
  fab2: {
    position: 'absolute',
    right: 0,
    bottom: 0, // Adjust as needed
  },
});

type HomeProps = NativeStackScreenProps<RootStackParamList, Route.HOME>;
const HomeScreen: React.FC<HomeProps> = () => {
  const snapPoints = useMemo(() => ['15%', '50%', '90%'], []);

  const [open, setOpen] = useState(true);

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

  return (
    <GestureHandlerRootView style={styles.container}>
      <MapView
        style={styles.map}
        initialRegion={isleOfWight}
        rotateEnabled={false}
      ></MapView>
      <Animated.View style={viewTextStyle}>
        {/* <FAB style={styles.fab2} icon="plus"></FAB> */}
        <FAB.Group
          open={open}
          visible
          style={styles.fab2}
          backdropColor={'transparent'}
          icon="plus"
          actions={[
            { icon: 'plus', onPress: () => setOpen(false) },
            {
              icon: 'star',
              onPress: () => console.log('Pressed star'),
            },
            {
              icon: 'email',
              onPress: () => console.log('Pressed email'),
            },
            {
              icon: 'bell',
              onPress: () => console.log('Pressed notifications'),
            },
          ]}
          onPress={() => setOpen(!open)}
          onStateChange={() => {}}
        />
      </Animated.View>
      <BottomSheet
        index={1}
        snapPoints={snapPoints}
        animatedPosition={bottomSheetPosition}
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

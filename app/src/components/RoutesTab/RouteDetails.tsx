import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import {
  getDistance,
  getMapDisplayRegion,
  getRouteSpeeds,
} from '../../models/routeModel';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { RoutesParamList } from './Routes';
import MapView, { Polyline } from 'react-native-maps';
import { routeColors } from '../../colors';
import interpolate from 'color-interpolate';
import { LinearGradient } from 'expo-linear-gradient';

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    paddingHorizontal: 10,
    paddingTop: 10,
    backgroundColor: 'white',
    height: '100%',
  },
  arrowContainer: {
    marginRight: 10,
  },
  icon: {
    color: 'blue',
  },
  textContainer: {
    flex: 1,
  },
  routeName: {
    fontSize: 20,
    fontWeight: 'bold',
  },
  distance: {
    fontSize: 16,
    color: '#888',
    marginTop: 5,
  },
  map: {
    width: '100%',
    height: 300,
  },
  linearGradient: {
    flex: 1,
    borderRadius: 5,
  },
});

type RouteDetailsProps = NativeStackScreenProps<
  RoutesParamList,
  'RouteDetails'
>;

const RouteDetails: React.FC<RouteDetailsProps> = ({
  route,
  navigation,
}: RouteDetailsProps) => {
  const mapRoute = route.params.route;
  const region = getMapDisplayRegion(mapRoute);

  const speeds = getRouteSpeeds(mapRoute);

  const minSpeed = Math.min(...speeds);
  const maxSpeed = Math.max(...speeds);
  const range = maxSpeed - minSpeed;

  const normalisedSpeeds = speeds.map((speed) => (speed - minSpeed) / range);

  const colourChoices = ['blue', 'green', 'red'];

  const colourmap = interpolate(colourChoices);
  const colours = normalisedSpeeds.map((speed) => colourmap(speed));
  return (
    <View style={styles.container}>
      <TouchableOpacity
        style={styles.arrowContainer}
        onPress={() => navigation.navigate('RouteList')}
      >
        <FontAwesomeIcon icon={faArrowLeft} style={styles.icon} />
      </TouchableOpacity>
      <View style={styles.textContainer}>
        <Text style={styles.routeName}>{mapRoute.name}</Text>
        <Text
          style={styles.distance}
        >{`Distance covered2: ${getDistance(mapRoute)}km`}</Text>
      </View>
      <View>
        <LinearGradient
          colors={colourChoices}
          start={{ x: 0, y: 0 }}
          end={{ x: 1, y: 0 }}
          style={styles.linearGradient}
        />
      </View>
      <MapView style={styles.map} region={region}>
        {colours.map((colour, index) => {
          return (
            <Polyline
              key={index}
              coordinates={[
                mapRoute.locations[index],
                mapRoute.locations[index + 1],
              ]}
              strokeColor={colour}
              strokeWidth={3}
            ></Polyline>
          );
        })}
      </MapView>
    </View>
  );
};

export default RouteDetails;

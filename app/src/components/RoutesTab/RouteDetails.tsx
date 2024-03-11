import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { getMapDisplayRegion, getRouteSpeeds } from '../../models/routeModel';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { RoutesParamList } from './Routes';
import MapView, { Polyline } from 'react-native-maps';
import interpolate from 'color-interpolate';
import { LinearGradient } from 'expo-linear-gradient';
import { speedMapColours } from '../../colors';
import RouteInformation from './RouteInformation';
import { RouteInfoRow } from './RouteInfoRow';
import { BottomSheetScrollView } from '@gorhom/bottom-sheet';

const styles = StyleSheet.create({
  container: {
    flex: 1,
    margin: 10,
  },
  titleContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  routeName: {
    fontSize: 20,
    marginLeft: 5,
    fontWeight: 'bold',
  },
  gradientContainer: {
    marginTop: 10,
    flexDirection: 'row',
    alignItems: 'center',
  },
  linearGradient: {
    flex: 4,
    height: '100%',
    borderRadius: 10,
    borderWidth: 2,
    borderColor: 'black',
  },
  text: {
    textAlign: 'center',
    flex: 1,
    fontSize: 16,
  },
  divider: {
    height: 1,
    width: '100%',
    backgroundColor: 'grey',
    marginVertical: 5,
  },
  map: {
    width: '100%',
    height: '100%',
  },
  mapContainer: {
    width: '100%',
    height: 200,
    marginVertical: 10,
    borderRadius: 10,
    overflow: 'hidden',
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

  const colourmap = interpolate(speedMapColours);
  const colours = normalisedSpeeds.map((speed) => colourmap(speed));

  return (
    <View style={styles.container}>
      <View style={styles.titleContainer}>
        <TouchableOpacity onPress={() => navigation.navigate('RouteList')}>
          <FontAwesomeIcon icon={faArrowLeft} size={25} color={'blue'} />
        </TouchableOpacity>
        <Text style={styles.routeName}>{mapRoute.name}</Text>
      </View>
      <View style={styles.divider} />
      <RouteInfoRow
        route={mapRoute}
        timeDisplayStr={route.params.timeDisplayStr}
        showTime={true}
      />
      <BottomSheetScrollView>
        <View style={styles.gradientContainer}>
          <Text style={styles.text}>Slow</Text>
          <LinearGradient
            colors={speedMapColours}
            start={{ x: 0, y: 0 }}
            end={{ x: 1, y: 0 }}
            style={styles.linearGradient}
          />
          <Text style={styles.text}>Fast</Text>
        </View>
        <View style={styles.mapContainer}>
          <MapView
            style={styles.map}
            region={region}
            provider="google"
            zoomEnabled={false}
            scrollEnabled={false}
          >
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
        <View>
          <RouteInformation route={mapRoute}></RouteInformation>
        </View>
      </BottomSheetScrollView>
    </View>
  );
};

export default RouteDetails;

import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { getDistance } from '../../models/routeModel';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { RoutesParamList } from './Routes';
import MapView, { Polyline } from 'react-native-maps';

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
    height: 'auto',
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
        >{`Distance covered: ${getDistance(mapRoute)}km`}</Text>
        <MapView
          style={styles.map}
          // region={region}
        >
          {/* <Polyline
            coordinates={route.locations}
            strokeColor={routeColors.selected}
            strokeWidth={3}
          ></Polyline> */}
        </MapView>
      </View>
    </View>
  );
};

export default RouteDetails;

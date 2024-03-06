import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { getDistance, RouteModel } from '../../models/routeModel';
import { RouteListNavigationProp } from './Routes';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons';
import RouteInformation from '../RouteInformation';

export type RouteDetailsProps = {
  routes: RouteModel[] | undefined;
  selectedRouteIndex: number;
  navigation: RouteListNavigationProp;
};

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
});

const RouteDetails: React.FC<RouteDetailsProps> = ({
  routes,
  selectedRouteIndex,
  navigation,
}: RouteDetailsProps) => {
  if (routes === undefined) {
    return null;
  }
  return (
    <View style={styles.container}>
      <TouchableOpacity
        style={styles.arrowContainer}
        onPress={() => navigation.navigate('RouteList')}
      >
        <FontAwesomeIcon icon={faArrowLeft} style={styles.icon} />
      </TouchableOpacity>
      <View style={styles.textContainer}>
        <Text style={styles.routeName}>{routes[selectedRouteIndex].name}</Text>
        <Text
          style={styles.distance}
        >{`Distance covered: ${getDistance(routes[selectedRouteIndex])}km`}</Text>
      </View>

      {/* <RouteInformation route={routes[selectedRouteIndex]}></RouteInformation> */}
    </View>
  );
};

export default RouteDetails;

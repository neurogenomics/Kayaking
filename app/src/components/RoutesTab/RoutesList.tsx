import {
  FlatList,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { RouteModel } from '../../models/routeModel';
import React, { useCallback } from 'react';
import { RouteListNavigationProp } from './Routes';
import { RouteDetailsProps } from './RouteDetails';
import MapView, { Polyline } from 'react-native-maps';
import { Icon } from 'react-native-paper';
import { routeColors } from '../../colors';

const styles = StyleSheet.create({
  itemContainer: {
    padding: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    flexDirection: 'row',
  },
  contentContainer: {
    backgroundColor: 'white',
  },
  mapContainer: {
    flex: 1,
    borderRadius: 10,
    overflow: 'hidden',
  },
  mainText: {
    fontSize: 18,
    marginBottom: 4,
    textAlign: 'center',
  },
  rowContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    width: '100%',
  },
  textContainer: {
    justifyContent: 'center',
    flex: 1,
    alignItems: 'center',
    flexDirection: 'row',
    textAlign: 'center',
  },
  detailsContainer: {
    flex: 3,
    width: '100%',
    justifyContent: 'center',
  },
  map: {
    width: '100%',
    height: '100%',
  },
  text: {
    fontSize: 18,
    marginHorizontal: 3,
  },
});

export type RoutesListProps = {
  routes: RouteModel[] | undefined;
  selectedRouteIndex: number;
  setSelectedRouteIndex: React.Dispatch<React.SetStateAction<number>>;
  navigation: RouteListNavigationProp;
};

const RoutesList: React.FC<RoutesListProps> = ({
  routes,
  selectedRouteIndex,
  setSelectedRouteIndex,
  navigation,
}) => {
  // renders routes in a list where you can select a route
  const renderItem = useCallback(
    ({ item, index }: { item: RouteModel; index: number }) => {
      const route = item;
      const latitudes = route.locations.map((location) => location.latitude);
      const longitudes = route.locations.map((location) => location.longitude);
      const minLat = Math.min(...latitudes);
      const maxLat = Math.max(...latitudes);
      const minLng = Math.min(...longitudes);
      const maxLng = Math.max(...longitudes);

      // Calculate deltas for latitude and longitude
      const deltaLat = (maxLat - minLat) * 1.1; // Add some padding
      const deltaLng = (maxLng - minLng) * 1.1; // Add some padding

      // Calculate center of the region
      const centerLat = (minLat + maxLat) / 2;
      const centerLng = (minLng + maxLng) / 2;

      const region = {
        latitude: centerLat,
        longitude: centerLng,
        latitudeDelta: deltaLat,
        longitudeDelta: deltaLng,
      };

      const totalMins = Math.round(
        route.checkpoints[route.checkpoints.length - 1] / 60,
      );
      const mins = totalMins % 60;
      const hours = Math.floor(totalMins / 60);

      let timeDisplayStr = '';
      if (hours > 0) {
        timeDisplayStr = `${hours}h `;
      }
      timeDisplayStr += `${mins}m`;

      return (
        <TouchableOpacity onPress={() => selectRouteFromList(index)}>
          <View style={styles.itemContainer}>
            <View style={styles.mapContainer}>
              <MapView
                style={styles.map}
                region={region}
                scrollEnabled={false}
                zoomEnabled={false}
              >
                <Polyline
                  coordinates={route.locations}
                  strokeColor={routeColors.selected}
                  strokeWidth={3}
                ></Polyline>
              </MapView>
            </View>
            <View style={styles.detailsContainer}>
              <Text style={styles.mainText}>{route.name}</Text>
              <View style={styles.rowContainer}>
                <View style={styles.textContainer}>
                  <Icon source="kayaking" size={24} />
                  <Text style={styles.text}>
                    {(route.length / 1000).toFixed(1)}km
                  </Text>
                </View>
                <View style={styles.textContainer}>
                  <Icon source="clock-time-eight-outline" size={24} />
                  <Text style={styles.text}>{timeDisplayStr}</Text>
                </View>
                <View style={styles.textContainer}>
                  {/* TODO: Display difficulty of route here */}
                  <Text style={styles.text}>Easy</Text>
                </View>
              </View>
            </View>
          </View>
        </TouchableOpacity>
      );
    },
    [routes],
  );

  const selectRouteFromList = (index: number) => {
    if (routes !== undefined) {
      setSelectedRouteIndex(index);
      const props: RouteDetailsProps = {
        routes: routes,
        selectedRouteIndex: selectedRouteIndex,
        navigation: navigation,
      };
      navigation.navigate('RouteDetails', { props });
    }
  };

  return (
    <View>
      {routes === undefined ? (
        // TODO make better display than this
        <Text>Enter filters to get a route</Text>
      ) : (
        <FlatList
          data={routes}
          keyExtractor={(_item, index) => index.toString()}
          renderItem={renderItem}
          contentContainerStyle={styles.contentContainer}
        />
      )}
    </View>
  );
};
export default RoutesList;

import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { RouteModel, getMapDisplayRegion } from '../../models/routeModel';
import React from 'react';
import { RouteListNavigationProp } from './Routes';
import MapView, { Polyline } from 'react-native-maps';
import { RouteInfoRow } from './RouteInfoRow';
import { routeVisualisationColors } from '../../colors';

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'red',
  },
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
  mainTextSelected: {
    fontSize: 18,
    fontWeight: 'bold',
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
  routes: RouteModel[];
  navigation: RouteListNavigationProp;
  setSelectedRouteIndex: React.Dispatch<React.SetStateAction<number>>;
  selectedRouteIndex: number;
};

const RoutesList: React.FC<RoutesListProps> = ({
  routes,
  navigation,
  setSelectedRouteIndex,
  selectedRouteIndex,
}) => {
  const renderItem = (route: RouteModel, index: number) => {
    const region = getMapDisplayRegion(route);
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
      <TouchableOpacity
        onPress={() => {
          navigation.navigate('RouteDetails', {
            route: routes[index],
            timeDisplayStr: timeDisplayStr,
            startTime: routes[index].startTime,
          });
          setSelectedRouteIndex(index);
        }}
        key={index}
      >
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
                strokeColor={
                  routeVisualisationColors[
                    index % routeVisualisationColors.length
                  ]
                }
                strokeWidth={3}
              ></Polyline>
            </MapView>
          </View>
          <View style={styles.detailsContainer}>
            <Text
              style={
                selectedRouteIndex === index
                  ? styles.mainTextSelected
                  : styles.mainText
              }
            >
              {route.name}
            </Text>
            <RouteInfoRow
              route={routes[selectedRouteIndex]}
              timeDisplayStr={timeDisplayStr}
              startTime={routes[selectedRouteIndex].startTime}
            />
          </View>
        </View>
      </TouchableOpacity>
    );
  };

  return (
    <View style={styles.contentContainer}>
      {routes.length === 0 ? (
        <Text>
          No routes found. Try changing filters or zoom out on the map.
        </Text>
      ) : (
        <View style={styles.contentContainer}>{routes.map(renderItem)}</View>
      )}
    </View>
  );
};
export default RoutesList;

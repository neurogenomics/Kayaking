import {
  FlatList,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { getDistance, RouteModel } from '../../models/routeModel';
import React, { useCallback } from 'react';
import { RouteListNavigationProp } from './Routes';

const styles = StyleSheet.create({
  itemContainer: {
    padding: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
    height: 75,
  },
  contentContainer: {
    backgroundColor: 'white',
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
    ({ item, index }: { item: RouteModel; index: number }) => (
      <TouchableOpacity onPress={() => selectRouteFromList(index)}>
        <View style={styles.itemContainer}>
          <Text>Route {index + 1}</Text>
          <Text>{`Distance covered: ${getDistance(item)}km`}</Text>
        </View>
      </TouchableOpacity>
    ),
    [routes],
  );

  const selectRouteFromList = (index: number) => {
    setSelectedRouteIndex(index);
    const selectedRoute = routes[selectedRouteIndex];
    navigation.navigate('RouteDetails', { selectedRoute, selectedRouteIndex });
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

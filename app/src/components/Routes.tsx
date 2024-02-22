import React, { useCallback } from 'react';
import { View, Text, StyleSheet, FlatList } from 'react-native';
import { RouteModel } from '../models/routeModel';

const styles = StyleSheet.create({
  itemContainer: {
    padding: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
  },
});

type RoutesProps = {
  routes: RouteModel[] | undefined;
  setSelectedRouteIndex: React.Dispatch<React.SetStateAction<number>>; // if you have clicked on item of list this gets set to that index
  // TODO consider display if routes undefined
};
const Routes: React.FC<RoutesProps> = ({ routes }: RoutesProps) => {
  const renderItem = useCallback(
    ({ item, index }: { item: RouteModel; index: number }) => (
      <View style={styles.itemContainer}>
        <Text>Route {index + 1}</Text>
        <Text>{`Distance covered: ${Math.round(item.length / 1000)}km`}</Text>
      </View>
    ),
    [routes],
  );

  return (
    <View>
      {routes === undefined ? (
        <Text>Enter filters to get a route</Text>
      ) : (
        <FlatList
          data={routes}
          keyExtractor={(item, index) => index.toString()}
          renderItem={renderItem}
          contentContainerStyle={{
            backgroundColor: 'white',
          }}
        />
      )}
    </View>
  );
};
export default Routes;

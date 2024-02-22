import React, { useCallback } from 'react';
import { View, Text, StyleSheet, FlatList } from 'react-native';
import { RouteModel } from '../models/routeModel';

const styles = StyleSheet.create({
  itemContainer: {
    padding: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
    height: 75,
  },
});

type RoutesProps = {
  routes: RouteModel[] | undefined;
  setSelectedRouteIndex: React.Dispatch<React.SetStateAction<number>>; // if you have clicked on item of list this gets set to that index
};
const Routes: React.FC<RoutesProps> = ({ routes }: RoutesProps) => {
  const renderItem = useCallback(
    ({ item, index }: { item: RouteModel; index: number }) => (
      <View style={styles.itemContainer}>
        <Text>Route {index + 1}</Text>
        <Text>{`Distance covered: ${(item.length / 1000).toFixed(2)}km`}</Text>
      </View>
    ),
    [routes],
  );

  return (
    <View>
      {routes === undefined ? (
        // why isnt this displaying?
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

import React, { useCallback } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { RouteModel } from '../models/routeModel';
import { BottomSheetFlatList } from '@gorhom/bottom-sheet';

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
      <Text>Route stuff goes here</Text>
      {routes?.map((item, index) => (
        <View style={styles.itemContainer} key={index}>
          <Text>Route {index + 1}</Text>
          <Text>{`Distance covered: ${Math.round(item.length / 1000)}km`}</Text>
        </View>
      ))}
      <BottomSheetFlatList
        data={routes}
        keyExtractor={(item, index) => index.toString()}
        renderItem={renderItem}
        contentContainerStyle={{
          backgroundColor: 'white',
        }}
      />
    </View>
  );
};
export default Routes;

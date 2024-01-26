import React from 'react';
import { View, Text, StyleSheet, FlatList } from 'react-native';

const DisplayDataScreen: React.FC = ({ route }) => {
  // Extracting the parameters from the route
  const { longitude, latitude } = route.params;

  const data = [
    { title: 'Sunrise Time', value: '6:00 AM' },
    { title: 'Sunset Time', value: '8:00 PM' },
    { title: 'Closest Slipway', value: 'Isle of Wight' },
  ];

  return (
    <View style={styles.container}>
      <View style={styles.locationContainer}>
        <Text style={styles.locationText}>Longitude: {longitude}</Text>
        <Text style={styles.locationText}>Latitude: {latitude}</Text>
      </View>
      <View style={styles.dataContainer}>
        <Text style={styles.sectionTitle}>Data:</Text>
        <FlatList
          data={data}
          keyExtractor={(item, index) => index.toString()}
          renderItem={({ item }) => (
            <View style={styles.dataItem}>
              <Text>
                {item.title}: {item.value}
              </Text>
            </View>
          )}
        />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
  },
  locationContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 16,
  },
  locationText: {
    fontSize: 18,
  },
  dataContainer: {
    flex: 1,
  },
  sectionTitle: {
    fontSize: 20,
    marginBottom: 8,
  },
  dataItem: {
    fontSize: 18,
    marginBottom: 8,
  },
});

export default DisplayDataScreen;

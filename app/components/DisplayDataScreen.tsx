import React from 'react';
import { View, Text, StyleSheet, FlatList } from 'react-native';

interface DisplayDataScreenProps {
  sunrise: string;
  sunset: string;
  slipway: string;
  tide: string;
  wind: string;
}

const DisplayDataScreen: React.FC<DisplayDataScreenProps> = ({
  sunrise,
  sunset,
  slipway,
  tide,
  wind,
}) => {
  const data = [
    { title: 'Sunrise Time', value: sunrise },
    { title: 'Sunset Time', value: sunset },
    { title: 'Closest Slipway', value: slipway },
    { title: 'Tide', value: tide },
    { title: 'Wind', value: wind },
    { title: 'Low tide time', value: 'Coming soon...' },
    { title: 'High tide time', value: 'Coming soon...' },
  ];

  return (
    <View style={styles.container}>
      <View style={styles.dataContainer}>
        <Text style={styles.sectionTitle}>Data:</Text>
        <FlatList
          data={data}
          keyExtractor={(item, index) => index.toString()}
          renderItem={({ item }) => (
            <View style={styles.dataItem}>
              <Text style={{ fontWeight: 'bold' }}>{item.title}: </Text>
              <Text>{item.value}</Text>
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
  dataContainer: {
    flex: 1,
  },
  sectionTitle: {
    fontSize: 20,
    marginBottom: 8,
  },
  dataItem: {
    fontSize: 20,
    marginBottom: 8,
  },
});

export default DisplayDataScreen;

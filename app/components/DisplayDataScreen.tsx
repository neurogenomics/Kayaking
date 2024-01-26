import React from 'react';
import { View, Text, StyleSheet, FlatList } from 'react-native';

interface DisplayDataScreenProps {
  longitude: string;
  latitude: string;
  sunrise: string;
  sunset: string;
}

const DisplayDataScreen: React.FC<DisplayDataScreenProps> = ({ longitude, latitude, sunrise, sunset }) => {

  const data = [
    { title: 'Sunrise Time', value: sunrise },
    { title: 'Sunset Time', value: sunset },
    { title: 'Closest Slipway', value: 'Isle of Wight' },
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

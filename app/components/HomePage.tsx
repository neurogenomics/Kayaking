// HomePage.tsx
import React, {useState} from 'react';
import { View, Text, Image, Button, StyleSheet} from 'react-native';
import kayakingImage from '../assets/kayaking.png';
import { SunsetInfo } from '../src/models/sunsetModel';
import { LocationModel, locationToString } from '../src/models/locationModel';
import { getSunset } from '../src/services/sunsetService';
import DisplayDataScreen from './DisplayDataScreen';
import { MapVisualisation } from './MapVisualisation';
import { getClosestSlipway } from '../src/services/slipwayService';
import { Vector, vectorToString } from '../src/models/vectorModel';
import { getTideDirection } from '../src/services/tideService';
import { InputLocation } from './InputLocation';
import { getWindDirection } from '../src/services/windService';

export const HomePage: React.FC = () => {
  const [longitude, setLongitude] = useState('');
  const [latitude, setLatitude] = useState('');
  const [showData, setShowData] = useState(false);

  const location: LocationModel = {
    latitude: parseFloat(longitude),
    longitude: parseFloat(latitude),
  };

  const handleSaveLocation = () => {
    console.log('Latitude: ', location.latitude);
    console.log('Longitude: ', location.longitude);
    setShowData(true);
  };

  return (
    <View style={styles.container}>
      {/*TODO: uncomment once mapview is sorted*/}
      {/*{!showData && <Text style={styles.title}>Welcome, Kayaker!</Text>}*/}
      {/*<Image source={kayakingImage} style={{ width: 200, height: 200 }} />*/}
      {/*<Text style={styles.title}>Enter your location below:</Text>*/}
      {/*<InputLocation setLongitude={setLongitude} setLatitude={setLatitude} />*/}
      {/*<Button title="Save Location" onPress={handleSaveLocation} />*/}
      <MapVisualisation lon={location.longitude} lat={location.latitude} />
      {/*{showData && (*/}
      {/*  <DisplayDataScreen*/}
      {/*    sunrise={sunrise}*/}
      {/*    sunset={sunset}*/}
      {/*    slipway={slipway}*/}
      {/*    tide={tide}*/}
      {/*    wind={wind}*/}
      {/*  />*/}
      {/*)}*/}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
  title: {
    fontSize: 20,
    margin: 5,
    textAlign: 'center',
  },
});

export default HomePage;

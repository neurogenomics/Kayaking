// HomePage.tsx
import React, { useState } from 'react';
import { View, Text, Image, Button, StyleSheet } from 'react-native';
import kayakingImage from '../assets/kayaking.png';
import { SunsetInfo } from '../src/models/sunsetModel';
import { LocationModel, locationToString } from '../src/models/locationModel';
import { getSunset } from '../src/services/sunsetService';
import DisplayDataScreen from './DisplayDataScreen';
import { getClosestSlipway } from '../src/services/slipwayService';
import { Vector, vectorToString } from '../src/models/vectorModel';
import { getTideDirection } from '../src/services/tideService';
import { InputLocation } from './InputLocation';
import { getWindDirection } from '../src/services/windService';

const HomePage: React.FC = () => {
  const [longitude, setLongitude] = useState('');
  const [latitude, setLatitude] = useState('');
  const [sunrise, setSunrise] = useState('No sunrise data yet');
  const [sunset, setSunset] = useState('No sunset data yet');
  const [slipway, setSlipway] = useState('No slipway data yet');
  const [tide, setTide] = useState('No tide data yet');
  const [wind, setWind] = useState('No wind data yet');
  const [showData, setShowData] = useState(false);

  const location: LocationModel = {
    latitude: parseFloat(longitude),
    longitude: parseFloat(latitude),
  };

  const updateSunset = async () => {
    try {
      const sunsetInfo: SunsetInfo = await getSunset(location);
      setSunset(sunsetInfo.sunset.toString());
      setSunrise(sunsetInfo.sunrise.toString());
      console.log('Sunset data updated successfully');
    } catch (error) {
      console.error('Error updating sunset data: ', error);
    }
  };

  const updateSlipway = async () => {
    try {
      const slipwayInfo: LocationModel = await getClosestSlipway(location);
      setSlipway(locationToString(slipwayInfo));
      console.log('Slipway data updated successfully');
    } catch (error) {
      console.error('Error updating slipway data: ', error);
    }
  };

  const updateTide = async () => {
    try {
      const tideInfo: Vector = await getTideDirection(location);
      setTide(vectorToString(tideInfo));
      console.log('Tide data updated successfully');
    } catch (error) {
      console.error('Error updating tide data: ', error);
    }
  };

  const updateWind = async () => {
    try {
      const windInfo: Vector = await getWindDirection(location);
      setWind(vectorToString(windInfo));
      console.log('Wind data updated successfully');
    } catch (error) {
      console.error('Error updating wind data: ', error);
    }
  };

  const handleSaveLocation = () => {
    updateSunset();
    updateSlipway();
    updateTide();
    updateWind();
    setShowData(true);
  };

  return (
    <View style={styles.container}>
      {!showData && <Text style={styles.title}>Welcome, Kayaker!</Text>}
      <Image source={kayakingImage} style={{ width: 200, height: 200 }} />
      <Text style={styles.title}>Enter your location below:</Text>
      <InputLocation setLongitude={setLongitude} setLatitude={setLatitude} />
      <Button title="Save Location" onPress={handleSaveLocation} />
      {showData && (
        <DisplayDataScreen
          sunrise={sunrise}
          sunset={sunset}
          slipway={slipway}
          tide={tide}
          wind={wind}
        />
      )}
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

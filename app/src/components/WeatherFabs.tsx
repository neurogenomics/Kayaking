import { FAB } from 'react-native-paper';
import React, { useState } from 'react';
import { StyleSheet } from 'react-native';
import { COLORS } from '../colors';
import { GridType } from '../models/gridModel';

const styles = StyleSheet.create({
  fabGroup: {
    paddingBottom: 0,
    width: '100%',
  },
  fab: {
    backgroundColor: COLORS.fabUnselected,
  },
});

type WeatherFabsProps = {
  visible: boolean;
  setWeatherMap: React.Dispatch<React.SetStateAction<GridType | undefined>>;
  setSunsetOn: React.Dispatch<React.SetStateAction<boolean>>;
  setTideTimesOn: React.Dispatch<React.SetStateAction<boolean>>;
};

export enum WeatherFab {
  SUNSET = 'Sunset',
  WAVE_HEIGHT = 'Wave Height',
  TIDE = 'Tide',
  WIND = 'Wind',
}

const WeatherFabs: React.FC<WeatherFabsProps> = ({
  visible,
  setWeatherMap,
  setSunsetOn,
  setTideTimesOn,
}: WeatherFabsProps) => {
  const [layersOpen, setLayersOpen] = useState(false);
  const icons = [
    'weather-sunset',
    'waves-arrow-up',
    'waves-arrow-right',
    'weather-windy',
  ];
  const names = [
    WeatherFab.SUNSET,
    WeatherFab.WAVE_HEIGHT,
    WeatherFab.TIDE,
    WeatherFab.WIND,
  ];
  const [layers, setLayers] = useState(new Array(icons.length).fill(false));

  const handleWeatherPress = (weather: GridType) => {
    setWeatherMap((prevWeather) => {
      if (prevWeather && prevWeather !== weather) {
        setLayers((prevState) => ({
          ...prevState,
          [names.findIndex(
            (name) => (name as string) === (prevWeather as string),
          )]: false,
        }));
      }
      return prevWeather === weather ? undefined : weather;
    });
  };

  return (
    <FAB.Group
      open={layersOpen && visible}
      visible={visible}
      style={styles.fabGroup}
      fabStyle={styles.fab}
      icon="layers"
      backdropColor={COLORS.backdrop}
      actions={icons.map((icon, index) => ({
        icon: icon,
        label: names[index],
        onPress: () => {
          switch (names[index]) {
            case WeatherFab.WIND: {
              handleWeatherPress(GridType.WIND);
              break;
            }
            case WeatherFab.TIDE: {
              handleWeatherPress(GridType.TIDE);
              break;
            }
            case WeatherFab.SUNSET: {
              setSunsetOn((prevState) => !prevState);
              break;
            }
            case WeatherFab.WAVE_HEIGHT: {
              setTideTimesOn((prevState) => !prevState);
              break;
            }
          }
          setLayers((prevState) => ({
            ...prevState,
            [index]: !prevState[index],
          }));
        },
        style: {
          backgroundColor: layers[index]
            ? COLORS.fabSelected
            : COLORS.fabUnselected,
        },
      }))}
      onPress={() => setLayersOpen(!layersOpen)}
      onStateChange={() => {}}
    />
  );
};

export default WeatherFabs;

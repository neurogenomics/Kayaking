import { FAB } from 'react-native-paper';
import React, { useState } from 'react';
import { StyleSheet } from 'react-native';
import { COLORS } from '../colors';
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
};

const WeatherFabs: React.FC<WeatherFabsProps> = ({
  visible,
}: WeatherFabsProps) => {
  const [layersOpen, setLayersOpen] = useState(false);
  const icons = [
    'weather-sunset',
    'waves-arrow-up',
    'waves-arrow-right',
    'weather-windy',
  ];
  const names = ['Sunset', 'Wave Height', 'Tide', 'Wind'];
  const [layers, setLayers] = useState(new Array(icons.length).fill(false));
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

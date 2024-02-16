import MapView, { Marker } from 'react-native-maps';
import { StyleSheet, View, Image, ImageSourcePropType } from 'react-native';
import React, { useState, useEffect } from 'react';
import { UserInput } from '../models/userInputModel';
import { LocationModel } from '../models/locationModel';
import { isleOfWight } from '../../constants';
import {
  GridModel,
  GridType,
  ResolutionModel,
  CoordRotation,
} from '../models/gridModel';
import { getGrid } from '../services/gridService';
import arrow from '../assets/arrow.png';

type MapVisualisationProps = {
  navigation;
  route: {
    params: { user: UserInput };
  };
};

export const MapVisualisation: React.FC<MapVisualisationProps> = () => {
  const [coords, setCoords] = useState<CoordRotation[]>();

  // TODO: get constants from server
  const gridStart: LocationModel = {
    latitude: 49.37,
    longitude: -1.8,
  };
  const gridEnd: LocationModel = {
    latitude: 51,
    longitude: 0,
  };
  const gridResolution: ResolutionModel = {
    latRes: 0.1,
    lonRes: 0.1,
  };

  const getArrowGrid = async () => {
    try {
      const grid: GridModel = await getGrid(
        GridType.WIND,
        gridStart,
        gridEnd,
        gridResolution,
      );
      setArrowCoords(grid);
    } catch (error) {
      console.log('Error getting grid: ', error);
    }
  };

  const getBearing = (u: number, v: number) => {
    const angleRadians = Math.atan2(u, v);
    let angleDegrees = angleRadians * (180 / Math.PI);
    angleDegrees =
      angleDegrees < 0
        ? Math.round(angleDegrees + 360)
        : Math.round(angleDegrees);
    return angleDegrees;
  };

  const setArrowCoords = (grid: GridModel) => {
    const markers: CoordRotation[] = [];
    for (let i = 0; i < grid.latIndex.length; i++) {
      for (let j = 0; j < grid.lonIndex.length; j++) {
        const direction = getBearing(grid.grid[i][j].u, grid.grid[i][j].v);
        const coordRotation: CoordRotation = {
          coord: {
            latitude: grid.latIndex[i],
            longitude: grid.lonIndex[j],
          },
          direction: direction,
        };
        markers.push(coordRotation);
      }
    }
    setCoords(markers);
  };

  useEffect(() => {
    void getArrowGrid();
  }, []);

  return (
    <View style={styles.mapContainer}>
      <MapView
        style={styles.map}
        initialRegion={isleOfWight}
        rotateEnabled={false}
      >
        {coords ? (
          coords.map((coord, index) => (
            <View key={index}>
              <Marker coordinate={coord.coord}>
                <Image
                  source={arrow as ImageSourcePropType}
                  style={{
                    width: 30,
                    height: 30,
                    objectFit: 'contain',
                    transform: [{ rotate: `${coord.direction}deg` }],
                  }}
                />
              </Marker>
            </View>
          ))
        ) : (
          <View />
        )}
      </MapView>
    </View>
  );
};

const styles = StyleSheet.create({
  mapContainer: {
    flex: 1,
    height: '100%',
    width: '100%',
    alignItems: 'center',
    justifyContent: 'center',
  },
  map: {
    flex: 1,
    height: '50%',
    width: '100%',
    alignItems: 'center',
    justifyContent: 'center',
  },
});

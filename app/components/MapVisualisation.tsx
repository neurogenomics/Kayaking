import MapView, { Polyline } from 'react-native-maps';
import { StyleSheet, View } from 'react-native';
import React, { useState, useEffect } from 'react';
import { UserInput } from '../src/models/userInputModel';
import { LocationModel } from '../src/models/locationModel';
import { isleOfWight } from '../constants';
import { GridModel, GridType, ResolutionModel } from '../src/models/gridModel';
import { getGrid } from '../src/services/gridService';

type MapVisualisationProps = {
  navigation;
  route: {
    params: { user: UserInput };
  };
};

type ArrowCoords = {
  coords: LocationModel[];
};

export const MapVisualisation: React.FC<MapVisualisationProps> = () => {
  const [coords, setCoords] = useState<ArrowCoords[]>();

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

  const makeArrowCoordinates = (grid: GridModel, gridRes: ResolutionModel) => {
    const markers: ArrowCoords[] = [];
    for (let i = 0; i < grid.latIndex.length; i++) {
      for (let j = 0; j < grid.lonIndex.length; j++) {
        const latitude: number = grid.latIndex[i];
        const longitude: number = grid.lonIndex[j];
        // TODO: add direction to arrows
        const direction = getBearing(grid.grid[i][j].u, grid.grid[i][j].v);
        const bottom: LocationModel = {
          longitude: longitude,
          latitude: latitude - gridRes.latRes / 2,
        };
        console.log("bottom")
        console.log(bottom)
        const top: LocationModel = {
          longitude: longitude,
          latitude: latitude + gridRes.latRes / 2,
        };
        console.log("top")
        console.log(top)
        const x: LocationModel[] = [bottom, top];
        const arrowPoints: ArrowCoords = { coords: x };
        markers.push(arrowPoints);
      }
    }
    setCoords(markers);
  };

  const getArrowGrid = async () => {
    try {
      console.log('hello???');
      const grid: GridModel = await getGrid(
        GridType.WIND,
        gridStart,
        gridEnd,
        gridResolution,
      );
      console.log('hello');
      makeArrowCoordinates(grid, gridResolution);
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

  useEffect(() => {
    console.log('hi');
    getArrowGrid();
  }, []);

  return (
    <View style={styles.mapContainer}>
      <MapView style={styles.map} initialRegion={isleOfWight}>
        {coords ? (
          coords.map((coord: ArrowCoords, index) => (
            <View key={index}>
              <Polyline coordinates={coord.coords}></Polyline>
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

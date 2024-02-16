import MapView, { Marker, Polyline } from 'react-native-maps';
import { StyleSheet, View } from 'react-native';
import React, { useState, useEffect } from 'react';
import { UserInput } from '../src/models/userInputModel';
import { LocationModel } from '../src/models/locationModel';
import { isleOfWight } from '../constants';
import { GridModel, GridType, ResolutionModel } from '../src/models/gridModel';
import { getGrid } from '../src/services/gridService';
import * as math from 'mathjs';

type MapVisualisationProps = {
  navigation;
  route: {
    params: { user: UserInput };
  };
};

type ArrowCoords = {
  coords: LocationModel[];
  magnitude: number;
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
    latRes: 0.05,
    lonRes: 0.05,
  };

  // TODO: move these to colours file
  const windColours: string[] = [
    'rgb(173, 216, 230)',
    'rgb(135, 206, 250)',
    'rgb(0, 191, 255)',
    'rgb(30, 144, 255)',
    'rgb(0, 0, 255)',
  ];

  // Colours arrows according to the Beaufort scale
  const getColour = (magnitude: number) => {
    if (magnitude <= 1) {
      return windColours[0];
    } else if (magnitude <= 3) {
      return windColours[1];
    } else if (magnitude <= 6) {
      return windColours[2];
    } else if (magnitude <= 10) {
      return windColours[3];
    }
    return windColours[4];
  };

  const rotateAroundPoint = (
    loc: math.Matrix,
    origin: math.Matrix,
    thetaRad: number,
  ): LocationModel => {
    const diff = math.subtract(loc, origin);

    // Clockwise rotation matrix
    const rotationMat = math.matrix([
      [Math.cos(thetaRad), Math.sin(thetaRad)],
      [-Math.sin(thetaRad), Math.cos(thetaRad)],
    ]);

    const rotated = math.add(math.multiply(rotationMat, diff), origin);

    const rotatedModel: LocationModel = {
      latitude: rotated.get([0]),
      longitude: rotated.get([1]),
    };

    return rotatedModel;
  };

  const makeArrowCoordinates = (grid: GridModel, gridRes: ResolutionModel) => {
    const markers: ArrowCoords[] = [];
    for (let i = 0; i < grid.latIndex.length; i++) {
      for (let j = 0; j < grid.lonIndex.length; j++) {
        const latitude: number = grid.latIndex[i];
        const longitude: number = grid.lonIndex[j];

        // Coordinates of right facing arrow
        const left = math.matrix([latitude, longitude - gridRes.lonRes / 3]);
        const right = math.matrix([latitude, longitude + gridRes.lonRes / 3]);
        const top = math.matrix([
          latitude + gridRes.latRes / 9,
          longitude + gridRes.lonRes / 6,
        ]);
        const bottom = math.matrix([
          latitude - gridRes.latRes / 9,
          longitude + gridRes.lonRes / 6,
        ]);

        const coordinates = [left, right, top, right, bottom];

        // Bearing angle that the arrow needs to be rotated by
        const theta = Math.atan2(grid.grid[i][j].v, grid.grid[i][j].u);

        // Origin around which arrow is rotated
        const origin = math.matrix([latitude, longitude]);

        // Magnitude of vector
        const magnitude: number = math.sqrt(
          grid.grid[i][j].u ** 2 + grid.grid[i][j].v ** 2,
        );

        const rotatedCoords: LocationModel[] = [];
        for (let i = 0; i < coordinates.length; i++) {
          rotatedCoords.push(rotateAroundPoint(coordinates[i], origin, theta));
        }

        const arrowPoints: ArrowCoords = {
          coords: rotatedCoords,
          magnitude: magnitude,
        };
        markers.push(arrowPoints);
      }
    }
    setCoords(markers);
  };

  const getArrowGrid = async () => {
    try {
      const grid = await getGrid(
        GridType.WIND,
        gridStart,
        gridEnd,
        gridResolution,
      );
      makeArrowCoordinates(grid, gridResolution);
    } catch (error) {
      console.log('Error getting grid: ', error);
    }
  };

  useEffect(() => {
    void getArrowGrid();
  }, []);

  return (
    <View style={styles.mapContainer}>
      <MapView style={styles.map} initialRegion={isleOfWight}>
        {coords ? (
          coords.map((coord: ArrowCoords, index) => (
            <View key={index}>
              <Polyline
                coordinates={coord.coords}
                strokeWidth={1.5}
                strokeColor={getColour(coord.magnitude)}
              ></Polyline>
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

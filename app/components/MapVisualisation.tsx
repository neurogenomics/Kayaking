import MapView, { Polyline, PROVIDER_GOOGLE } from 'react-native-maps';
import { StyleSheet, View } from 'react-native';
import React, { useEffect, useState } from 'react';
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

type Arrow = {
  left: LocationModel;
  right: LocationModel;
  top: LocationModel;
  bottom: LocationModel;
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

  // TODO: put this in colours file
  interface ColorMap {
    [category: string]: string;
  }

  const windColorMap: ColorMap = {
    Calm: 'rgb(173, 216, 230)', // Light Blue
    'Light Breeze': 'rgb(135, 206, 250)', // Sky Blue
    'Gentle Breeze': 'rgb(0, 191, 255)', // Deep Sky Blue
    'Moderate Breeze': 'rgb(30, 144, 255)', // Dodger Blue
    'Fresh Breeze': 'rgb(0, 0, 255)', // Blue
  };

  // Colours arrows according to the Beaufort scale
  const getColour = (magnitude: number) => {
    if (magnitude <= 1) {
      return windColorMap['Calm'];
    } else if (magnitude <= 3) {
      return windColorMap['Light Breeze'];
    } else if (magnitude <= 6) {
      return windColorMap['Gentle Breeze'];
    } else if (magnitude <= 10) {
      return windColorMap['Moderate Breeze'];
    }
    return windColorMap['Fresh Breeze'];
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

  function getArrow(
    left: math.Matrix,
    right: math.Matrix,
    top: math.Matrix,
    origin: math.Matrix,
    thetaRad: number,
  ): Arrow {
    // Rotating arrow according to angle
    const leftModel = rotateAroundPoint(left, origin, thetaRad);
    const rightModel = rotateAroundPoint(right, origin, thetaRad);
    const topModel = rotateAroundPoint(top, origin, thetaRad);

    const slope: number =
      (rightModel.latitude - leftModel.latitude) /
      (rightModel.longitude - leftModel.longitude);
    const perpSlope: number = -1 / slope;

    const perpEquation = (longitude) =>
      perpSlope * (longitude - topModel.longitude) + topModel.latitude;

    const intersectLon =
      (1 / (slope - perpSlope)) *
      (slope * leftModel.longitude -
        leftModel.latitude -
        perpSlope * topModel.longitude +
        topModel.latitude);
    const intersectLat = perpEquation(intersectLon);

    // Create a matrix for the intersection point
    const intersection = math.matrix([intersectLat, intersectLon]);

    const topMat = math.matrix([topModel.latitude, topModel.longitude]);

    const bottomModel: LocationModel = rotateAroundPoint(
      topMat,
      intersection,
      Math.PI,
    );

    return {
      left: leftModel,
      right: rightModel,
      top: topModel,
      bottom: bottomModel,
    };
  }

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

        // Bearing angle that the arrow needs to be rotated by
        const theta = Math.atan2(grid.grid[i][j].v, grid.grid[i][j].u);

        // Origin around which arrow is rotated
        const origin = math.matrix([latitude, longitude]);

        // Magnitude of vector
        const magnitude: number = math.sqrt(
          grid.grid[i][j].u ** 2 + grid.grid[i][j].v ** 2,
        );

        const arrow: Arrow = getArrow(left, right, top, origin, theta);

        const arrowPoints: ArrowCoords = {
          coords: [
            arrow.left,
            arrow.right,
            arrow.top,
            arrow.right,
            arrow.bottom,
          ],
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
      <MapView
        style={styles.map}
        initialRegion={isleOfWight}
        provider={PROVIDER_GOOGLE}
        rotateEnabled={true}
      >
        {coords ? (
          coords.map((coord: ArrowCoords, index) => (
            <View key={index}>
              <Polyline
                coordinates={coord.coords}
                strokeWidth={2}
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

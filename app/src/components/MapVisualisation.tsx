import { Polyline } from 'react-native-maps';
import { StyleSheet, View } from 'react-native';
import React, { useEffect, useState } from 'react';
import { LocationModel } from '../models/locationModel';
import { GridModel, GridType, ResolutionModel } from '../models/gridModel';
import { getGrid } from '../services/gridService';
import { Matrix, Vector } from 'ts-matrix';
import { tideColorMap, windColorMap } from '../colors';

type MapVisualisationProps = {
  navigation;
  route: {
    params: { display: GridType };
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

export const MapVisualisation: React.FC<MapVisualisationProps> = ({
  route,
}) => {
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
  // TODO: add feature so resolution increases as user zooms
  const gridResolution: ResolutionModel = {
    latRes: 0.05,
    lonRes: 0.05,
  };

  const rotateAroundPoint = (
    loc: Vector,
    origin: Vector,
    thetaRad: number,
  ): LocationModel => {
    const diff = loc.subtract(origin);
    const diffMat = new Matrix(2, 1, [[diff.values[0]], [diff.values[1]]]);

    // Clockwise rotation matrix
    const rotationMat = new Matrix(2, 2, [
      [Math.cos(thetaRad), Math.sin(thetaRad)],
      [-Math.sin(thetaRad), Math.cos(thetaRad)],
    ]);
    const flattenMul = rotationMat
      .multiply(diffMat)
      .values.reduce((accumulator, value) => accumulator.concat(value));
    const rotated: Vector = new Vector(flattenMul);
    const final: Vector = rotated.add(origin);

    return {
      latitude: final.values[0],
      longitude: final.values[1],
    };
  };

  function getArrow(
    left: Vector,
    right: Vector,
    top: Vector,
    origin: Vector,
    thetaRad: number,
  ): Arrow {
    // Rotating arrow according to angle
    const leftModel = rotateAroundPoint(left, origin, thetaRad);
    const rightModel = rotateAroundPoint(right, origin, thetaRad);
    const topModel = rotateAroundPoint(top, origin, thetaRad);

    // Maths to reflect the top vertex of the arrow along the arrow line to get the bottom vertex
    const slope: number =
      (rightModel.latitude - leftModel.latitude) /
      (rightModel.longitude - leftModel.longitude);
    const perpSlope: number = -1 / slope;

    const perpEquation = (longitude: number) =>
      perpSlope * (longitude - topModel.longitude) + topModel.latitude;

    const intersectLon =
      (1 / (slope - perpSlope)) *
      (slope * leftModel.longitude -
        leftModel.latitude -
        perpSlope * topModel.longitude +
        topModel.latitude);
    const intersectLat = perpEquation(intersectLon);

    // Create a matrix for the intersection point
    const intersection = new Vector([intersectLat, intersectLon]);
    const topMat = new Vector([topModel.latitude, topModel.longitude]);

    // Rotating top point 180 degrees around the intersection point to get the new point
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
        const left = new Vector([latitude, longitude - gridRes.lonRes / 3]);
        const right = new Vector([latitude, longitude + gridRes.lonRes / 3]);
        const top = new Vector([
          latitude + gridRes.latRes / 9,
          longitude + gridRes.lonRes / 6,
        ]);

        // Bearing angle that the arrow needs to be rotated by
        const theta = Math.atan2(grid.grid[i][j].v, grid.grid[i][j].u);

        // Origin around which arrow is rotated
        const origin = new Vector([latitude, longitude]);

        // Magnitude of wind/tide vector
        const magnitude: number = Math.sqrt(
          grid.grid[i][j].u ** 2 + grid.grid[i][j].v ** 2,
        );

        const arrow = getArrow(left, right, top, origin, theta);

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
        route.params.display,
        gridStart,
        gridEnd,
        gridResolution,
      );
      makeArrowCoordinates(grid, gridResolution);
    } catch (error) {
      console.log('Error getting grid: ', error);
    }
  };

  const getColor = (magnitude: number, palette: GridType): string => {
    const map = palette === GridType.WIND ? windColorMap : tideColorMap;
    for (const category of map) {
      if (magnitude <= category.maxMagnitude) {
        return category.color;
      }
    }
    return 'black';
  };

  useEffect(() => {
    void getArrowGrid();
  }, []);

  return (
    <View style={styles.mapContainer}>
      {coords ? (
        coords.map((coord: ArrowCoords, index) => (
          <View key={index}>
            <Polyline
              coordinates={coord.coords}
              strokeWidth={2}
              strokeColor={getColor(coord.magnitude, route.params.display)}
            ></Polyline>
          </View>
        ))
      ) : (
        <View />
      )}
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
});

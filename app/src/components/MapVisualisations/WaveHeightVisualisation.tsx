import React, { useEffect, useState } from 'react';
import { ResolutionModel } from '../../models/weatherGridModel';
import { LocationModel } from '../../models/locationModel';
import { Polygon } from 'react-native-maps';
import { mapVisColours } from '../../colors';
import { interpolateColor } from 'react-native-reanimated';
import { getWaveGrid } from '../../services/waveGridService';
import { WaveGridModel } from '../../models/waveGridModel';

type WaveHeightVisualisationProps = {
  date: Date;
};

type WaveHeightPolygon = {
  coords: LocationModel[];
  height: number;
};

export const WaveHeightVisualisation: React.FC<
  WaveHeightVisualisationProps
> = ({ date }) => {
  const [polyCoords, setPolyCoords] = useState<WaveHeightPolygon[]>();
  const [minHeight, setMinHeight] = useState<number>();
  const [maxHeight, setMaxHeight] = useState<number>();

  // TODO: get constants from server
  const gridStart: LocationModel = {
    latitude: 50.448253,
    longitude: -1.770676,
  };
  const gridEnd: LocationModel = {
    latitude: 50.934747,
    longitude: -0.842729,
  };
  // TODO: add feature so resolution increases as user zooms
  const gridResolution: ResolutionModel = {
    latRes: 0.05,
    lonRes: 0.05,
  };

  const getPolygons = (grid: WaveGridModel, gridRes: ResolutionModel) => {
    const waveHeights: WaveHeightPolygon[] = [];
    let minHeight = Infinity;
    let maxHeight = 0;

    for (let i = 0; i < grid.grid.length; i++) {
      for (let j = 0; j < grid.grid[i].length; j++) {
        if (grid.grid[i][j] && grid.latIndex[i] && grid.lonIndex[j]) {
          const latitude: number = grid.latIndex[i];
          const longitude: number = grid.lonIndex[j];

          minHeight = Math.min(minHeight, grid.grid[i][j].height);
          maxHeight = Math.max(maxHeight, grid.grid[i][j].height);

          const bottomLeft = {
            latitude: latitude - gridRes.latRes,
            longitude: longitude - gridRes.lonRes,
          };

          const bottomRight = {
            latitude: latitude - gridRes.latRes,
            longitude: longitude + gridRes.lonRes,
          };

          const topLeft = {
            latitude: latitude + gridRes.latRes,
            longitude: longitude - gridRes.latRes,
          };

          const topRight = {
            latitude: latitude + gridRes.latRes,
            longitude: longitude + gridRes.lonRes,
          };

          const waveHeight: WaveHeightPolygon = {
            coords: [bottomLeft, bottomRight, topRight, topLeft],
            height: grid.grid[i][j].height,
          };
          waveHeights.push(waveHeight);
        }
      }
    }
    setPolyCoords(waveHeights);
    setMinHeight(minHeight);
    setMaxHeight(maxHeight);
  };

  const getPolyColor = (height: number) => {
    if (minHeight !== undefined && maxHeight !== undefined) {
      const scale = (height - minHeight) / (maxHeight - minHeight);
      return interpolateColor(scale, [0, 1], mapVisColours.wave);
    }
  };

  const getWaveHeightGrid = async (date: Date) => {
    try {
      const grid = await getWaveGrid(gridStart, gridEnd, gridResolution, date);
      getPolygons(grid, gridResolution);
    } catch (error) {
      console.log('Error getting grid: ', error);
    }
  };

  useEffect(() => {
    setPolyCoords([]);
    void getWaveHeightGrid(date);
  }, [date]);

  return (
    <>
      {polyCoords
        ? polyCoords.map((coord, index) => (
            <Polygon
              key={index}
              coordinates={coord.coords}
              strokeColor={getPolyColor(coord.height)}
              fillColor={getPolyColor(coord.height)}
            />
          ))
        : null}
    </>
  );
};

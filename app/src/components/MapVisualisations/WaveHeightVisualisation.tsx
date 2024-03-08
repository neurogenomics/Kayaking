import React, { useEffect, useState } from 'react';
import { ResolutionModel } from '../../models/weatherGridModel';
import { LocationModel } from '../../models/locationModel';
import { Polygon } from 'react-native-maps';
import { getInterpolatedColor, mapVisColours } from '../../colors';
import { getWaveGrid } from '../../services/waveGridService';
import { WaveGridModel } from '../../models/waveGridModel';
import { gridStart, gridEnd, gridResolution } from '../../../constants';

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

  // According to the Douglas wave height scale
  const maxHeight = 3;
  const waveHeightScale = [0, maxHeight];

  const getPolygons = (grid: WaveGridModel, gridRes: ResolutionModel) => {
    const waveHeights: WaveHeightPolygon[] = [];

    for (let i = 0; i < grid.grid.length; i++) {
      for (let j = 0; j < grid.grid[i].length; j++) {
        if (grid.grid[i][j] && grid.latIndex[i] && grid.lonIndex[j]) {
          const latitude: number = grid.latIndex[i];
          const longitude: number = grid.lonIndex[j];

          // TODO: Check that the wave height service provides the top right corner of the grid
          const topRight = {
            latitude: latitude,
            longitude: longitude,
          };

          const bottomLeft = {
            latitude: latitude - gridRes.latRes,
            longitude: longitude - gridRes.lonRes,
          };

          const bottomRight = {
            latitude: latitude - gridRes.latRes,
            longitude: longitude,
          };

          const topLeft = {
            latitude: latitude,
            longitude: longitude - gridRes.lonRes,
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
    void getWaveHeightGrid(date);
  }, [date]);

  return (
    <>
      {polyCoords
        ? polyCoords.map((coord, index) => (
            <Polygon
              key={index}
              coordinates={coord.coords}
              strokeColor={getInterpolatedColor(
                coord.height,
                waveHeightScale,
                mapVisColours.wave,
              )}
              fillColor={getInterpolatedColor(
                coord.height,
                waveHeightScale,
                mapVisColours.wave,
              )}
              tappable={true}
            />
          ))
        : null}
    </>
  );
};

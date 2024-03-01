import { Polyline } from 'react-native-maps';
import React, { useEffect, useState } from 'react';
import { LocationModel } from '../../models/locationModel';
import { GridModel, GridType, ResolutionModel } from '../../models/gridModel';
import { getGrid } from '../../services/gridService';
import { tideColorMap, windColorMap } from '../../colors';

type WeatherVisualisationProps = {
  display: GridType;
  date: Date;
};

type WeatherVector = {
  location: LocationModel;
  direction: number;
  magnitude: number;
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

export const WeatherVisualisation: React.FC<WeatherVisualisationProps> = ({
  display,
  date,
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

  const getArrow = (
    vectors: WeatherVector[],
    gridRes: ResolutionModel,
    minMag: number,
    maxMag: number,
  ) => {
    const arrows: ArrowCoords[] = [];
    const maxLength: number = Math.min(gridRes.latRes, gridRes.lonRes);
    for (let i = 0; i < vectors.length; i++) {
      const vec = vectors[i];
      const x = (maxLength * (vec.magnitude - minMag)) / (maxMag - minMag);
      const endArrow: LocationModel = {
        longitude: vec.location.longitude + x * Math.sin(vec.direction),
        latitude: vec.location.latitude + x * Math.cos(vec.direction),
      };
      const coords = {
        coords: [vec.location, endArrow],
        magnitude: vec.magnitude,
      };
      arrows.push(coords);
    }
    setCoords(arrows);
  };

  const makeArrowCoordinates = (grid: GridModel) => {
    const vectors: WeatherVector[] = [];
    let minMag: number = Infinity;
    let maxMag: number = 0;
    for (let i = 0; i < grid.latIndex.length; i++) {
      for (let j = 0; j < grid.lonIndex.length; j++) {
        if (grid.grid[i][j] && grid.latIndex[i] && grid.lonIndex[j]) {
          const latitude: number = grid.latIndex[i];
          const longitude: number = grid.lonIndex[j];

          // Coordinates of right facing arrow
          // Constants chosen to prevent arrow from filling entire grid

          // Bearing angle that the arrow needs to be rotated by
          const theta = Math.atan2(grid.grid[i][j].v, grid.grid[i][j].u);

          // Magnitude of wind/tide vector
          const magnitude = Math.sqrt(
            grid.grid[i][j].u ** 2 + grid.grid[i][j].v ** 2,
          );

          minMag = Math.min(minMag, magnitude);
          maxMag = Math.max(maxMag, magnitude);

          const vec: WeatherVector = {
            location: { latitude: latitude, longitude: longitude },
            direction: theta,
            magnitude: magnitude,
          };
          vectors.push(vec);
        }
      }
    }
    getArrow(vectors, gridResolution, minMag, maxMag);
  };

  const getArrowGrid = async (date: Date) => {
    try {
      const grid = await getGrid(
        display,
        gridStart,
        gridEnd,
        gridResolution,
        date,
      );
      console.log(grid);
      makeArrowCoordinates(grid);
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
    if (display !== null) {
      void getArrowGrid(date);
    }
  }, [display, date]);

  return (
    <>
      {coords
        ? coords.map((coord, index) => (
            <Polyline
              key={index}
              coordinates={coord.coords}
              strokeWidth={2}
              strokeColor={getColor(coord.magnitude, display)}
              zIndex={0}
            />
          ))
        : null}
    </>
  );
};

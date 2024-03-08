import { Polyline } from 'react-native-maps';
import React, { useEffect, useState } from 'react';
import { LocationModel } from '../../models/locationModel';
import {
  WeatherGridModel,
  WeatherGridType,
  ResolutionModel,
} from '../../models/weatherGridModel';
import { getWeatherGrid } from '../../services/weatherGridService';
import { interpolateColor } from 'react-native-reanimated';
import { mapVisColours } from '../../colors';

type WeatherVisualisationProps = {
  display: WeatherGridType;
  date: Date;
};

type WeatherVector = {
  location: LocationModel;
  direction: number;
  magnitude: number;
};

type ArrowCoords = {
  coords: LocationModel[];
  scale: number;
};

export const WeatherVisualisation: React.FC<WeatherVisualisationProps> = ({
  display,
  date,
}) => {
  const [coords, setCoords] = useState<ArrowCoords[]>();

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

  const getArrows = (
    vectors: WeatherVector[],
    gridRes: ResolutionModel,
    minMag: number,
    maxMag: number,
  ) => {
    const arrows: ArrowCoords[] = [];

    // minimum length of arrow - set to 1/10th grid resolution
    const minLength: number = Math.min(gridRes.latRes, gridRes.lonRes) / 10;

    // maximum length of arrow - set to grid resolution
    const maxLength: number = Math.min(gridRes.latRes, gridRes.lonRes);

    // proportion of arrow covered by arrowhead
    const arrowHeadProportion: number = 1 / 3;

    // angle between arrowhead at arrow
    const arrowheadAngle = Math.PI / 6;
    for (let i = 0; i < vectors.length; i++) {
      const vec = vectors[i];

      // scale of vector according to magnitude (normalised between [0, 1])
      const scale = (vec.magnitude - minMag) / (maxMag - minMag);
      const arrowLength = scale * (maxLength - minLength) + minLength;
      const arrowHeadLength = arrowHeadProportion * arrowLength;

      const endArrow: LocationModel = {
        longitude:
          vec.location.longitude + arrowLength * Math.sin(vec.direction),
        latitude: vec.location.latitude + arrowLength * Math.cos(vec.direction),
      };

      const arrowheadPoint1: LocationModel = {
        longitude:
          endArrow.longitude -
          arrowHeadLength * Math.sin(vec.direction + arrowheadAngle),
        latitude:
          endArrow.latitude -
          arrowHeadLength * Math.cos(vec.direction + arrowheadAngle),
      };

      const arrowheadPoint2: LocationModel = {
        longitude:
          endArrow.longitude -
          arrowHeadLength * Math.sin(vec.direction - arrowheadAngle),
        latitude:
          endArrow.latitude -
          arrowHeadLength * Math.cos(vec.direction - arrowheadAngle),
      };

      const coords = {
        coords: [
          vec.location,
          endArrow,
          arrowheadPoint1,
          endArrow,
          arrowheadPoint2,
        ],
        scale: scale,
      };
      arrows.push(coords);
    }
    setCoords(arrows);
  };

  const getWeatherVectors = (grid: WeatherGridModel) => {
    const vectors: WeatherVector[] = [];
    let minMag: number = Infinity;
    let maxMag: number = 0;
    for (let i = 0; i < grid.latIndex.length; i++) {
      for (let j = 0; j < grid.lonIndex.length; j++) {
        if (grid.grid[i][j] && grid.latIndex[i] && grid.lonIndex[j]) {
          const latitude: number = grid.latIndex[i];
          const longitude: number = grid.lonIndex[j];

          // Bearing angle that the arrow needs to be rotated by
          const theta = Math.atan2(grid.grid[i][j].v, grid.grid[i][j].u);

          // Magnitude of wind/tide vector
          const magnitude = Math.sqrt(
            grid.grid[i][j].u ** 2 + grid.grid[i][j].v ** 2,
          );

          // Finding the minimum and maximum magnitudes in order to normalise the vectors
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
    getArrows(vectors, gridResolution, minMag, maxMag);
  };

  const getArrowColour = (scale: number) => {
    const outputRange =
      display === WeatherGridType.WIND
        ? mapVisColours.wind
        : mapVisColours.tide;
    return interpolateColor(scale, [0, 1], outputRange);
  };

  const getArrowGrid = async (date: Date) => {
    try {
      const grid = await getWeatherGrid(
        display,
        gridStart,
        gridEnd,
        gridResolution,
        date,
      );
      getWeatherVectors(grid);
    } catch (error) {
      console.log('Error getting grid: ', error);
    }
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
              strokeWidth={1 + coord.scale * 2}
              strokeColor={getArrowColour(coord.scale)}
              zIndex={0}
            />
          ))
        : null}
    </>
  );
};

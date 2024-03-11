import { interpolateColor } from 'react-native-reanimated';
import { Difficulty } from './models/routeModel';

export const colors = {
  fabUnselected: '#fafafa',
  fabSelected: '#6ca0dc',
  backdrop: '#c8dfeaaa',
};

export const mapVisColours = {
  wind: ['#0000FF', '#FF0000'],
  tide: ['#006f3c', '#0000FF'],
  wave: [
    'rgba(0, 255, 0, 0.25)', // Green with 50% transparency
    'rgba(255, 0, 0, 0.25)', // Red with 50% transparency
  ],
};

export const difficultyColours = {
  [Difficulty.Easy]: 'rgba(0, 128, 0, 0.3)',
  [Difficulty.Medium]: 'rgba(255, 165, 0, 0.3)',
  [Difficulty.Hard]: 'rgba(255, 0, 0, 0.3)',
};

export const speedMapColours = [
  '#264b96',
  '#27b376',
  '#006f3c',
  '#f9a73e',
  '#bf212f',
];

export const routeVisualisationColors: string[] = [
  '#FF6347', // Tomato
  '#40E0D0', // Turquoise
  '#4682B4', // Steel Blue
  '#7FFFD4', // Aquamarine
  '#00FFFF', // Cyan
  '#9370DB', // Medium Purple
  '#20B2AA', // Light Sea Green
  '#32CD32', // Lime Green
  '#FFD700', // Gold
  '#FFA07A', // Light Salmon
  '#FF69B4', // Hot Pink
  '#7B68EE', // Medium Slate Blue
  '#ADFF2F', // Green Yellow
  '#BA55D3', // Medium Orchid
  '#FF4500', // Orange Red
  '#00BFFF', // Deep Sky Blue
  '#FF8C00', // Dark Orange
  '#4169E1', // Royal Blue
  '#8A2BE2', // Blue Violet
];

export const getInterpolatedColor = (
  val: number,
  scale: number[],
  colourScale: string[],
) => {
  const max = scale[scale.length - 1];
  const num = val > max ? max : val;
  return interpolateColor(num, scale, colourScale);
};

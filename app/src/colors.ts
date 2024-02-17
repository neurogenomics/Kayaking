export const COLORS = {
  fabUnselected: '#fafafa',
  fabSelected: '#6ca0dc',
  backdrop: '#c8dfeaaa',
};

interface WeatherCategory {
  rating: string;
  maxMagnitude: number;
  color: string;
}

export const windColorMap: WeatherCategory[] = [
  { rating: 'Calm', maxMagnitude: 1, color: 'rgb(173, 216, 230)' }, // Light Blue
  { rating: 'Light Breeze', maxMagnitude: 3, color: 'rgb(135, 206, 250)' }, // Sky Blue
  { rating: 'Gentle Breeze', maxMagnitude: 6, color: 'rgb(0, 191, 255)' }, // Deep Sky Blue
  { rating: 'Moderate Breeze', maxMagnitude: 10, color: 'rgb(30, 144, 255)' }, // Dodger Blue
  { rating: 'Fresh Breeze', maxMagnitude: Infinity, color: 'rgb(0, 0, 255)' }, // Blue
];

export const tideColorMap: WeatherCategory[] = [
  { rating: 'Very Slow', maxMagnitude: 1, color: 'rgb(221, 160, 221)' }, // Plum
  { rating: 'Slow', maxMagnitude: 2, color: 'rgb(148, 0, 211)' }, // Dark Violet
  { rating: 'Moderate', maxMagnitude: 3, color: 'rgb(128, 0, 128)' }, // Purple
  { rating: 'Fast', maxMagnitude: 4, color: 'rgb(75, 0, 130)' }, // Indigo
  { rating: 'Very Fast', maxMagnitude: Infinity, color: 'rgb(72, 61, 139)' }, // Dark Slate Blue
];

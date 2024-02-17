export const COLORS = {
  fabUnselected: '#fafafa',
  fabSelected: '#6ca0dc',
  backdrop: '#c8dfeaaa',
};
interface ColorMap {
  [category: string]: string;
}

export const windColorMap: ColorMap = {
  Calm: 'rgb(173, 216, 230)', // Light Blue
  'Light Breeze': 'rgb(135, 206, 250)', // Sky Blue
  'Gentle Breeze': 'rgb(0, 191, 255)', // Deep Sky Blue
  'Moderate Breeze': 'rgb(30, 144, 255)', // Dodger Blue
  'Fresh Breeze': 'rgb(0, 0, 255)', // Blue
};

// Colours arrows according to the Beaufort scale
export const getWindColour = (magnitude: number) => {
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

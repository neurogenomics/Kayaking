import { getData } from './utils';

export const getWeatherDates = async (): Promise<Date[]> => {
  const url = 'times';
  return (await getData<string[]>(url)).map((dateStr) => new Date(dateStr));
};

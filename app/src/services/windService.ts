import { LocationModel } from '../models/locationModel';
import { Vector } from '../models/vectorModel';
import { getData, postData } from './utils';
import { format } from 'date-fns';

export const getWindDirection = async (
  location: LocationModel,
  date?: Date,
): Promise<Vector> => {
  let url = `wind?lat=${location.latitude}&lon=${location.longitude}`;
  if (date) {
    url += `&date=${format(date, "yyyy-MM-dd'T'HH:mm:ss")}`;
  }
  return await getData<Vector>(url);
};

export const getWindDangerousWind = async (
  locations: LocationModel[],
  checkpoints: number[],
  date: Date,
): Promise<boolean[]> => {
  const url = 'dangerouswind';
  return await postData<boolean[]>(url, {
    locations,
    checkpoints,
    date: format(date, "yyyy-MM-dd'T'HH:mm:ss"),
  });
};

export const getWindsDirection = async (
  locations: LocationModel[],
  checkpoints: number[],
  date: Date,
): Promise<Vector[]> => {
  console.log(locations);
  console.log(date);

  const payload = {
    locations: locations,
    checkpoints: checkpoints,
    start: date,
  };

  const url = 'winds';

  return await postData<Vector[]>(url, payload);
};

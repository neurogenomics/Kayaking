import axios, { AxiosRequestConfig } from 'axios';

const BACKEND_API_ORIGIN = process.env.EXPO_PUBLIC_BACKEND_URL;
const generateUrl = (path: string): string =>
  new URL(path, BACKEND_API_ORIGIN).toString();

export const getData = async <T>(
  path: string,
  options: AxiosRequestConfig = {},
): Promise<T> => {
  const url = generateUrl(path);
  const response = await axios.get<T>(url, options);
  return response.data;
};

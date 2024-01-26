import { StyleSheet } from 'react-native';
import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import { NavigationContainer } from '@react-navigation/native';
import HomePage from './components/HomePage';
import DisplayDataScreen from './components/DisplayDataScreen';

const Stack = createStackNavigator();

export const App: React.FC = () => {
  return <HomePage />;
};

export default App;

import { StyleSheet } from 'react-native';
import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import { NavigationContainer } from '@react-navigation/native';
import HomePage from './components/HomePage';
import DisplayDataScreen from './components/DisplayDataScreen';

const Stack = createStackNavigator();

export const App: React.FC = () => {
  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName="HomePage">
        <Stack.Screen name="Homepage" component={HomePage} />
        <Stack.Screen name="DisplayDataScreen" component={DisplayDataScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default App;

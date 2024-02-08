import React from 'react';
import AppNavigator from './components/AppNavigator';
import { NavigationContainer } from '@react-navigation/native';

export const App: React.FC = () => {
  return (
    <NavigationContainer>
      <AppNavigator />
    </NavigationContainer>
  );
};

export default App;

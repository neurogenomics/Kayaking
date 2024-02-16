import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { StatusBar } from 'react-native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { RootStackParamList, Route } from './src/routes';
import HomeScreen from './src/screens/Home';

const Stack = createNativeStackNavigator<RootStackParamList>();

export const App: React.FC = () => {
  return (
    <NavigationContainer>
      <StatusBar translucent={true}></StatusBar>
      <Stack.Navigator
        initialRouteName={Route.HOME}
        screenOptions={{
          headerShown: false,
        }}
      >
        <Stack.Screen name={Route.HOME} component={HomeScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default App;

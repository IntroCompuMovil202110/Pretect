import { createSwitchNavigator } from '@react-navigation/compat';
import { NavigationContainer } from '@react-navigation/native';
import React from 'react';
import AuthNavigator from './src/screens/AuthNavigator.js';
import TabNavigator from './src/screens/TabNavigator.js';

const SwitchNavigator = createSwitchNavigator(
  {
    Auth: AuthNavigator,
    App: TabNavigator,
  },
  {
    initialRouteName: 'Auth'
  }
);

const AppContainer = () => {
  
  return(
    <NavigationContainer>
      <SwitchNavigator />
    </NavigationContainer>
  )

};
export default AppContainer;

import React from 'react';
import { createMaterialBottomTabNavigator } from '@react-navigation/material-bottom-tabs';
import { MaterialCommunityIcons } from '@expo/vector-icons';
import { Colors } from 'react-native-paper';
import HomeScreen from './HomeScreen.js';
import AgeScreen from './AgeScreen.js';
import TimeScreen from './TimeScreen.js';

const Tab = createMaterialBottomTabNavigator();

const TabNavigator = () => {
  return(
    <Tab.Navigator
      initialRouteName= 'Home'
      activeColor='#F3F3F3'
      barStyle={{ backgroundColor: Colors.purple900}}
    >
      <Tab.Screen 
        name='Home'
        component={HomeScreen}
        options={{
          tabBarLabel: 'Mapa',
          tabBarIcon: ({ color }) => (<MaterialCommunityIcons name="map" size={26} color={color} />)
        }}
        />
      <Tab.Screen 
        name='Age'
        component={AgeScreen}
        options={{
          tabBarLabel: 'Edades',
          tabBarIcon: ({ color }) => (<MaterialCommunityIcons name="numeric" size={26} color={color} />)
        }}
        />
      <Tab.Screen 
        name='Time'
        component={TimeScreen}
        options={{
          tabBarLabel: 'Horas',
          tabBarIcon: ({ color }) => (<MaterialCommunityIcons name="clock-time-five-outline" size={26} color={color} />)
        }}
        />
    </Tab.Navigator>
  );
};

export default TabNavigator;

import React from 'react';
import { StyleSheet, SafeAreaView, View} from 'react-native';
import { Text, TextInput, Button } from 'react-native-paper';
import Svg, { G, Path, Circle } from "react-native-svg";
import * as d3 from "d3";
import firebase from '../../firebase/fire.js';

const HomeScreen = ({ navigation }) => {

  const signOutUser = async() => {
    try {
     const response = firebase.default.auth().signOut();
     navigation.navigate('Auth');
    } catch (error) {
      console.log(error.message);
    }
  };

  return(
    <View style={ styles.container }>
      <Button onPress={signOutUser}>
        Sign Out
      </Button>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    //justifyContent: 'center'
  }
});

export default HomeScreen;

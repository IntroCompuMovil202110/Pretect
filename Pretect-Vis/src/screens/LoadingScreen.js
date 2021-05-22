import React, { useEffect } from 'react';
import { View, StyleSheet } from 'react-native';
import { ActivityIndicator, Colors } from 'react-native-paper'; 
import firebase from '../../firebase/fire.js';

const LoadingScreen = ({ navigation }) => {

  useEffect(() => {
    firebase.default.auth().onAuthStateChanged( user => {
      if(user){
        navigation.navigate('App');
      }else {
        navigation.navigate('SignIn');
      }
    });
  });


  return (
    <View style={ styles.containter }>
      <ActivityIndicator animating={true} color={Colors.purple700}/>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center'
  }
});


export default LoadingScreen;

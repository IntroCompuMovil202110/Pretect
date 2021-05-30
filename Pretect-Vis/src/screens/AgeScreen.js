import React, { useEffect, useState } from 'react';
import { View, StyleSheet } from 'react-native';
import { VictoryBar, VictoryChart, VictoryTheme } from "victory-native";
import firebase from '../../firebase/fire.js';
import { Text, TextInput, Button } from 'react-native-paper';

const AgeScreen = ({ navigation }) => {

  const datos = [
    { rango: '0-10', frecuencia: 0 },
    { rango: '11-20', frecuencia: 0 },
    { rango: '21-30', frecuencia: 0 },
    { rango: '31-40', frecuencia: 0 },
    { rango: '41-50', frecuencia: 0 },
    { rango: '51-60', frecuencia: 0 },
    { rango: '61-70', frecuencia: 0 },
    { rango: '71+', frecuencia: 0 },
  ];

  const loadata = () => {
    const mLogs = firebase.default.database().ref("logs");

    mLogs.on("value", function(snapshot) {
      snapshot.forEach(function(data) {
          if(data.val().age > 0 && data.val().age <= 10){
            let objIndex = datos.findIndex((obj => obj.rango == '0-10'));
            datos[objIndex].frecuencia += 1;
          }
          else if(data.val().age >= 11 && data.val().age <= 20){
            let objIndex = datos.findIndex((obj => obj.rango == '11-20'));
            datos[objIndex].frecuencia += 1;
          }
          else if(data.val().age >= 21 && data.val().age <= 30){
            let objIndex = datos.findIndex((obj => obj.rango == '21-30'));
            datos[objIndex].frecuencia += 1;
          }
          else if(data.val().age >= 31 && data.val().age <= 40){
            let objIndex = datos.findIndex((obj => obj.rango == '31-40'));
            datos[objIndex].frecuencia += 1;
          }
          else if(data.val().age >= 41 && data.val().age <= 50){
            let objIndex = datos.findIndex((obj => obj.rango == '41-50'));
            datos[objIndex].frecuencia += 1;
          }
          else if(data.val().age >= 51 && data.val().age <= 60){
            let objIndex = datos.findIndex((obj => obj.rango == '51-60'));
            datos[objIndex].frecuencia += 1;
          }
          else if(data.val().age >= 61 && data.val().age <= 70){
            let objIndex = datos.findIndex((obj => obj.rango == '61-70'));
            datos[objIndex].frecuencia += 1;
          }
          else{
            let objIndex = datos.findIndex((obj => obj.rango == '71+'));
            datos[objIndex].frecuencia += 1;
          }
      });
    });
  };

  useEffect(() => {
    loadata();
  });

  return(
    <View style={ styles.container }>
      <Text style = { styles.title }>
        {
          loadata()
        }
        Este diagrama nos permite identificar las edades de los usuarios que activan la alarma con mayor frecuencia</Text>
      <VictoryChart width={350} theme={VictoryTheme.material}>
          <VictoryBar style={{ data: { fill: '#673AB7'}}} horizontal data={datos} x="rango" y="frecuencia" />
      </VictoryChart>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center'
  },
  title:{
    marginVertical: 15,
    fontWeight: '400',
    fontSize: 24,
    color: '#434343',
    textAlign: 'center'
  },
});

export default AgeScreen;

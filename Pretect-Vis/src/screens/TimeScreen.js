import React from 'react';
import { View, StyleSheet } from 'react-native';
import { VictoryBar, VictoryChart, VictoryTheme } from "victory-native";
import { Text, TextInput, Button } from 'react-native-paper';
import firebase from '../../firebase/fire.js';

const TimeScreen = ({ navigation }) => {
 const signOutUser = async() => {
    try {
     const response = firebase.default.auth().signOut();
     navigation.navigate('Auth');
    } catch (error) {
      console.log(error.message);
    }
  } 
  const datos = [
    { rango: '00-02', frecuencia: 0 },
    { rango: '02-04', frecuencia: 0 },
    { rango: '04-06', frecuencia: 0 },
    { rango: '06-08', frecuencia: 0 },
    { rango: '08-10', frecuencia: 0 },
    { rango: '10-12', frecuencia: 0 },
    { rango: '12-14', frecuencia: 0 },
    { rango: '14-16', frecuencia: 0 },
    { rango: '16-18', frecuencia: 0 },
    { rango: '18-20', frecuencia: 0 },
    { rango: '20-22', frecuencia: 0 },
    { rango: '22-00', frecuencia: 0 },
  ];

  const loadata = () => {
    const mLogs = firebase.default.database().ref("logs");

    mLogs.on("value", function(snapshot) {
      snapshot.forEach(function(data) {
        let comp = data.child("time").val().hours
          if(comp > 0 && comp <= 2){
            let objIndex = datos.findIndex((obj => obj.rango == '00-02'));
            datos[objIndex].frecuencia += 1;
          }
          else if(comp >= 2 && comp <= 4){
            let objIndex = datos.findIndex((obj => obj.rango == '02-04'));
            datos[objIndex].frecuencia += 1;
          }
          else if(comp > 4 && comp <= 6){
            let objIndex = datos.findIndex((obj => obj.rango == '04-06'));
            datos[objIndex].frecuencia += 1;
          }
          else if(comp > 6 && comp <= 8){
            let objIndex = datos.findIndex((obj => obj.rango == '06-08'));
            datos[objIndex].frecuencia += 1;
          }
          else if(comp > 8 && comp <= 10){
            let objIndex = datos.findIndex((obj => obj.rango == '08-10'));
            datos[objIndex].frecuencia += 1;
          }
          else if(comp > 10 && comp <= 12){
            let objIndex = datos.findIndex((obj => obj.rango == '10-12'));
            datos[objIndex].frecuencia += 1;
          }
          else if(comp > 12 && comp <= 14){
            let objIndex = datos.findIndex((obj => obj.rango == '12-14'));
            datos[objIndex].frecuencia += 1;
          }
          else if(comp > 14 && comp <= 16){
            let objIndex = datos.findIndex((obj => obj.rango == '14-16'));
            datos[objIndex].frecuencia += 1;
          }
          else if(comp > 16 && comp <= 18){
            let objIndex = datos.findIndex((obj => obj.rango == '16-18'));
            datos[objIndex].frecuencia += 1;
          }
          else if(comp > 18 && comp <= 20){
            let objIndex = datos.findIndex((obj => obj.rango == '18-20'));
            datos[objIndex].frecuencia += 1;
          }
          else if(comp > 20 && comp <= 22){
            let objIndex = datos.findIndex((obj => obj.rango == '20-22'));
            datos[objIndex].frecuencia += 1;
          }
          else{
            let objIndex = datos.findIndex((obj => obj.rango == '22-00'));
            datos[objIndex].frecuencia += 1;
          }
      });
    });
  };
  return(
    <View style={ styles.container }>
      <Text style = { styles.title }>
        { loadata() }
        Este diagrama nos permite identificar las horas en las que los usuarios que activan la alarma con mayor frecuencia</Text>
      <VictoryChart width={350} theme={VictoryTheme.material}>
          <VictoryBar style={{ data: { fill: '#673AB7'}}} horizontal data={datos} x="rango" y="frecuencia" />
      </VictoryChart>
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

export default TimeScreen;

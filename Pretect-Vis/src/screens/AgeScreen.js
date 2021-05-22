import React from 'react';
import { View, StyleSheet, Text } from 'react-native';
import { VictoryBar, VictoryChart, VictoryTheme } from "victory-native";

const AgeScreen = ({ navigation }) => {
  
  const data = [
    { quarter: 1, earnings: 13000 },
    { quarter: 2, earnings: 16500 },
    { quarter: 3, earnings: 14250 },
    { quarter: 4, earnings: 19000 }
  ];

  return(
    <View style={ styles.container }>
      <Text style = { styles.title }>Este diagrama nos permite identificar las edades de los usuarios que activan la alarma con mayor frecuencia</Text>
      <VictoryChart width={350} theme={VictoryTheme.material}>
          <VictoryBar horizontal data={data} x="quarter" y="earnings" />
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

import React from 'react';
import { View, StyleSheet, Text } from 'react-native';
import { VictoryArea, VictoryStack } from "victory-native";


const TimeScreen = ({ navigation }) => {
  
  const data = [
    { quarter: 1, earnings: 13000 },
    { quarter: 2, earnings: 16500 },
    { quarter: 3, earnings: 14250 },
    { quarter: 4, earnings: 19000 }
  ];

  return(
    <View style={ styles.container }>
      <Text style = { styles.title }>Este diagrama nos permite identificar las horas en las que los usuarios que activan la alarma con mayor frecuencia</Text>
      <VictoryStack>
        <VictoryArea
          name="area1"
          width={400} height={400} padding={0}
          data={data}
          x="quarter" y="earnings"
        />
      </VictoryStack>
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

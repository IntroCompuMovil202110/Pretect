import React, { useEffect, useState } from 'react';
import { StyleSheet, SafeAreaView, View} from 'react-native';
import { Text, TextInput, Button } from 'react-native-paper';
import firebase from '../../firebase/fire.js';
import MapView from "react-native-map-clustering";
import { Marker } from "react-native-maps";

const initialRegion = {
  latitude: 4.652144,
  longitude: -74.093134,
  latitudeDelta: 0.25,
  longitudeDelta: 0.15
};

function renderMarkers() {
  const { latitude, longitude, latitudeDelta, longitudeDelta } = initialRegion;
  const mLogs = firebase.default.database().ref("logs");
  mLogs.on("value", function(snapshot){
    snapshot.forEach(function(data){
      <Marker
      coordinate={{
        latitude: data.val().latitude,
        longitude: data.val().longitude
      }}
    />
    })
  });

  //key={snapshot.key}
}

const HomeScreen = ({ navigation }) => {

  const [locations, setLocations] = useState([])
  var locAux = []

  const drawLocations = () => {
   
    return locAux.map((marker, index) => 
        <Marker
          key={index}
          coordinate={{
            latitude: marker.latitude,
            longitude: marker.longitude
          }}
        />
    );
  }

  const getLocations = () => {
    const mLogs = firebase.default.database().ref("logs");
    
    mLogs.on("value", function(snapshot){
      snapshot.forEach(function(data){
        locAux.push({ "latitude" : data.val().latitude, "longitude" : data.val().longitude })
      })
    });
    console.log(locAux)
  }

  useEffect(() => {
    
   });


  return(
    <View style={ styles.container }>
      <MapView style={styles.map} initialRegion={initialRegion}>
        {getLocations()}
        {drawLocations()}
      </MapView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    //alignItems: 'center',
    //justifyContent: 'center'
  },
  map: {
    width: "100%",
    height: "100%"
  }
});

export default HomeScreen;

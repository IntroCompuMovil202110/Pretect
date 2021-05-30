import React, { useState } from 'react';
import { View, StyleSheet, Image } from 'react-native';
import { Text, TextInput, Button } from 'react-native-paper';
import firebase from '../../firebase/fire.js';

const SignInScreen = ({ navigation }) => {

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [err, setError] = useState('');

  const signInUser = async() => {
    try{
      const response = await firebase.default.auth().signInWithEmailAndPassword(email, password);
      navigation.navigate('App');
    }catch(err) {
      setError(err.message);
    }

  }

  const logo = require('../../assets/logo.jpeg');

  return(
    <View style={ styles.container }>
      <Image
        source={logo}
        style = {{ width: '40%', height: '25%', marginTop: 30 }}
        />
      <Text style = { styles.title }>BIENVENIDO ADMINISTRADOR</Text>
      <TextInput
        label="Correo"
        mode='outlined'
        value={ email }
        style = { styles.sep }
        onChangeText={(newEmail) => setEmail(newEmail)}
        />
      <TextInput
        label="Password"
        mode='outlined'
        value={ password }
        style = { styles.sep }
        secureTextEntry={true}
        onChangeText={(newPassword) => setPassword(newPassword)}
        />
      <Button 
        mode="contained" 
        style = { styles.sep }
        onPress={ signInUser }
        >
        Ingresar
      </Button>
    </View>
  );

};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    //justifyContent: 'center'
  },
  title:{
    marginVertical: 15,
    fontWeight: '700',
    fontSize: 32,
    color: '#434343',
    textAlign: 'center'
  },
  sep: {
    marginVertical: 10,
    width: 290,
  }
});

export default SignInScreen;

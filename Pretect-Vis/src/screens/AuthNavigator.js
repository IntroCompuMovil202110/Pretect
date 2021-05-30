import { createSwitchNavigator } from '@react-navigation/compat';

import LoadingScreen from './LoadingScreen.js';
import SignInScreen from './SignInScreen.js';

const AuthNavigator  = createSwitchNavigator(
  {
    Loading: { screen: LoadingScreen },
    SignIn: { screen: SignInScreen }
  },
  {
    initialRouteName: 'Loading'
  }
);

export default AuthNavigator;

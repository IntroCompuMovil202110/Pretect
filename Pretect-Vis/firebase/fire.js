import * as firebase from 'firebase';

const firebaseConfig = {
    apiKey: "AIzaSyDKwOakTqMCaF3MtIgMQdeYeHl1dw-VEGw",
    authDomain: "pretect-a4da3.firebaseapp.com",
    databaseURL: "https://pretect-a4da3-default-rtdb.firebaseio.com",
    projectId: "pretect-a4da3",
    storageBucket: "pretect-a4da3.appspot.com",
    messagingSenderId: "584805333082",
    appId: "1:584805333082:web:cafe84b9dbf0db2bd30063",
    measurementId: "G-FSEJFW0NET"
} 

// if(!firebase.apps.length){
  firebase.default.initializeApp(firebaseConfig);
// }

export default firebase;

// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
import { getAuth } from "firebase/auth";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyAIGIuiBVbBmY9jxftaUXPXIGmXo2iyrQM",
  authDomain: "frontend-3275e.firebaseapp.com",
  projectId: "frontend-3275e",
  storageBucket: "frontend-3275e.firebasestorage.app",
  messagingSenderId: "152219274714",
  appId: "1:152219274714:web:1606959b74c5ae181d44e3",
  measurementId: "G-97YNBBQYYF"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const auth = getAuth(app)
const analytics = getAnalytics(app);

export {app, auth};
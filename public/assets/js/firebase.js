// Import the functions you need from the SDKs you need
import { initializeApp } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-app.js";
import { getAnalytics } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-analytics.js";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
    apiKey: "AIzaSyAFehSTdV4wQBcs5yAUKpddgrXo29Zdsig",
    authDomain: "stu-intellect.firebaseapp.com",
    projectId: "stu-intellect",
    storageBucket: "stu-intellect.appspot.com",
    messagingSenderId: "1057619796945",
    appId: "1:1057619796945:web:dd45e4ecdb7062a74636f0",
    measurementId: "G-PG1YFX9M7V"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
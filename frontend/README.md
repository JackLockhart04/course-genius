# Getting Started with Create React App

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## About

The frontend is build with React with a base of TypeScript. It is hosted on infinity free hosting at "https://coursegenius.free.nf" because it is free. May change to a real domain once the website is completed.

## To deploy

Connect to ftp server (I used filezilla)
Build by running `npm run build`
Connect to ftp server with filezilla or MobaXterm and use server-stuff.txt credentials
Transfer build files to ftp server dir "htdocs"
Make sure .htaccess from frontend dir stays in the htdocs dir on the server

## Available Scripts

In the project directory, you can run:

### `npm start`

Runs the app in the development mode.\
Uses .env.development
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

### `npm test`

Launches the test runner in the interactive watch mode.\
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### `npm run build`

Builds the app for production to the `build` folder.
Uses .env.production
It correctly bundles React in production mode and optimizes the build for the best performance.
Your app is ready to be deployed!

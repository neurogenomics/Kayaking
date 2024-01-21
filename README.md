# Kayaking
## Frontend
### Installation
To install, run:
```
cd app
yarn install
```
### Running the app
To run the app, use:
```
cd app
yarn start
```
Then download the ExpoGo app on your smartphone and scan the QR code displayed in the terminal
Alternatively use `yarn android` or `yarn ios` to emulate the app on your computer (requires emulators to be installed)

### Linting & testing
To lint run:
```
cd app
yarn lint
```
To test run:
```
cd app
yarn test
```

## Backend
### Running the server
To run the server locally, run the following commands:
```
cd backend
./gradlew run
```
The app should now be running on http://127.0.0.1:8080

Alternatively, open the backend folder in an IDE (Intellij Ultimate recommended) and use the run button.
### Continuous Deployment
Any changes pushed to the main branch, will be deployed to https://kayaking-aa6c532cb1b2.herokuapp.com/
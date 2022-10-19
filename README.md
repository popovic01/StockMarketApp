# StockMarketApp

The app uses dependency injection with Daeger-Hilt, CSV parsing with OpenCSV. It works with remote API using Retrofing, does local caching with Room and custom drawing on a canvas using Jetpack Compose. 

It also follows architectural guidelines and SOLID principles.

This project was done with the help of this video: https://www.youtube.com/watch?v=uLs2FxFSWU4&t=858s. 

I extend the app with additional functionalities: 
- user can select radio button to show active or inactive companies
- error handling and loading for company listings
- caching for company info 
- showing company's location on google maps (using Intent)

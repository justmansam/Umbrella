# Umbrella - Weather App

![ic1](https://user-images.githubusercontent.com/18261776/209551976-4fb2f1ae-a87c-46cf-9c03-4c6cb4e95c3c.png)


## About:

Umbrella is a simple weather application that shows current, feels-like and min/max weather values along with humidity, visibility, wind speed, sunrise/sunset time values of a specific location.
Users can have these information by either giving location permission to the app or typing the city name in the search bar.
Umbrella follows the google's app architecture best practices such as seperation of concerns(layered architecture), single source of truth, offline first, single activity, naming and testing best practices etc.

## Tech Stack:

* Kotlin
* Compose
* MVVM
* Dagger/Hilt
* Shared Preferences
* Retrofit/Moshi
* Coroutines
* State Flow
* Location Services
* Json to Kotlin Class Plugin
* Test -> JUnit (viewmodel & composeUI)
* Permissions -> Internet - Network - Coarse Location
* Credits -> openweathermap.org (API) - @jiaojiaoniu (FIGMA) - unnamed (well.. nicknamed) heroes of Stackoverflow


## Development Log:

For a simple app like this one, Shared Preferences is better choice since there is no need for querying in local database.
But when favorites feature is implemented, RoomDB will be used to make it work properly.
Instead of compose state, state flow is used because of it's compose free property and it works better handling with app termination (along with saved state handle).
Coarse location is used for location service, since it consumes less resources and does the job just fine.


## Screenshoots:

![ss1](https://user-images.githubusercontent.com/18261776/209551052-b99a0010-77ed-4391-ae22-61974d50271f.png)
![ss2](https://user-images.githubusercontent.com/18261776/209551063-8cd02648-17e8-4178-9a36-7ce4751e3b6d.png)
![ss3](https://user-images.githubusercontent.com/18261776/209551815-b563c9da-46bf-48ba-abc8-7342738a3121.png)

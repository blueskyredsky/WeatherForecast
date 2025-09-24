☀️ Weather Forecast
==================
This is a modern, modular weather application built for Android using Kotlin and Jetpack Compose. The app follows the MVVM (Model-View-ViewModel) architectural pattern and is designed to provide users with real-time weather data and forecasts. The application is structured into a core module for shared functionalities and a feature module for specific user-facing screens, allowing for a clean and scalable codebase.

# 📋 Features
1. Current Weather: This feature provides a comprehensive view of the current weather and a 24-hour hourly forecast. It includes:
    1.1. Automatic Location Detection: The app requests the user's current location to display weather data for their immediate surroundings.
    1.2. Search Functionality: Users can search for any location worldwide to get detailed weather information and forecasts.
2. Forecast: This feature expands on the hourly forecast by providing more detailed, long-term weather predictions for a selected location.

# 🏗️ Architecture
The project is built on the MVVM architectural pattern, ensuring a clear separation of concerns.

View: Composable functions that observe changes in the ViewModel and update the UI accordingly.

ViewModel: Manages the UI state, handles user actions, and interacts with the Repository layer.

Model: Represents the data structures used throughout the application, including network responses.

# 🧩 Modules
The project is organized into the following modules to promote reusability and maintainability:

app: The main application module.

core: Contains common and reusable components.

common: General utility classes and UI components.

location: Handles all location-related logic, including requesting permissions and fetching coordinates.

network: Manages API calls and data fetching from the weather service.

threading: Provides components for managing coroutines and background operations.

feature: Contains the distinct feature modules.

currentweather: Implements the current weather and search functionality.

forecast: Implements the detailed weather forecast screen.

# ⚙️ Technologies and Libraries
The application leverages a modern Android development stack:

Language: Kotlin

UI Framework: Jetpack Compose for a declarative UI.

Asynchronous Operations: Kotlin Coroutines for managing background tasks.

Dependency Injection: Hilt for a clean and scalable DI setup.

Networking:

Retrofit for type-safe HTTP client.

OkHttp for efficient network requests.

Kotlinx Serialization for JSON serialization/deserialization.

Location Services: Google Play Services Location for accurate location data.

Image Loading: Coil for image loading and caching.

Permissions: Accompanist Permissions to handle runtime permissions gracefully.

Navigation: Jetpack Navigation for app navigation.

# 🎨 Design
The UI design is inspired by the following Figma community design: Weather App for iOS or Android.

# 🚀 Getting Started
Clone the repository.

Open the project in Android Studio.

Build and run the app module on an emulator or physical device.

Note: You will need a weather API key to fetch data. Make sure to configure it in your local environment.
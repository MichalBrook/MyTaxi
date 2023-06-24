# My Taxi Project
_**NOTE:** This is an educational project._

## About the project
Android application that makes it easy to calculate and compare prices between different taxi stations according to the ride distance, which is calculated using Google Maps services. The user, then, can choose the taxi station according to it's price and place the order. After placing the order, user can manage and pay the orders without leaving the application.

## Project documents
- [Project Book](./docs/book/)
- [UML Class Diagram](./docs/class-diagram/)
- [Firebase Structure and Data](./docs/data/)

## Advanced topics in the project
- Database: **Firebase Realtime Database**
- External API: **Google Maps Platform - Routes API**
- Data persistence: **Shared Preferences**
- System events: **Broadcast Receiver**

## Project dependencies
Database
  - [Firebase BoM](https://mvnrepository.com/artifact/com.google.firebase/firebase-bom)
  - [Firebase Database](https://mvnrepository.com/artifact/com.google.firebase/firebase-database)

HTTP Client
  - [OkHttp](https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp)

JSON Parser
  - [Jackson Databind](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind)

## Examples
### Google Maps Platform - Routes API
Request: `Content-Type: application/json`
```json
{
    "origin": {
        "address": "Ashdod"
    },
    "destination": {
        "address": "Holon"
    }
}
```
Response: `Content-Type: application/json`
```json
{
    "routes": [
        {
            "distanceMeters": 33829,
            "duration": "2069s"
        }
    ]
}
```

## Code Snippets
### Secrets used with API requests
File: `Secrets.java`
```java
/**
 * !!! DO NOT COMMIT THIS FILE TO REPO !!!
 * Add Secrets.java to .gitignore
 */

public class Secrets {
    public static String mapsApiUrl = "";
    public static String mapsApiKey = "";
}
```

### Allow HTTP requests in main thread
File: `AndroidManifest.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools" >

    <!-- Allow the application to access the internet -->
    <uses-permission android:name="android.permission.INTERNET"/>

    <application ...>
    </application>
</manifest>
```
File: `MainActivity.java`
```java
// Create permit nework policy
StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
// Apply the policy
StrictMode.setThreadPolicy(policy);
```

### Battery Level Broadcast Receiver configuration
File: `AndroidManifest.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application ...>

        <receiver
            android:name=".receivers.BatteryLevelReceiver"
            android:enabled="true"
            android:exported="true"
            android:permission="android.intent.action.BATTERY_LOW">
        </receiver>

    </application>
</manifest>
```

## References
Firebase
  - [Get Started](https://firebase.google.com/docs/database/android/start)
  - [Save Data](https://firebase.google.com/docs/database/admin/save-data)
  - [Retrieve Data](https://firebase.google.com/docs/database/admin/retrieve-data)

Google Maps API
  - [Google Maps Platform Documentation](https://developers.google.com/maps/documentation)
  - [Directions API (v1)](https://developers.google.com/maps/documentation/directions) - not used in the project
  - [Routes API (v2)](https://developers.google.com/maps/documentation/routes)

OkHttp
  - [Comparison of Java HTTP Clients](https://reflectoring.io/comparison-of-java-http-clients)

Jackson Databind
  - [JSON Parsers](https://stackoverflow.com/questions/2591098/how-to-parse-json-in-java/31743324#31743324)
  - [FasterXML Jackson](https://github.com/FasterXML/jackson)
  - [Jackson Databind](https://github.com/FasterXML/jackson-databind)

ListView ArrayAdapter
  - [Using an ArrayAdapter with ListView](https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView)
  - [Example](https://www.geeksforgeeks.org/arrayadapter-in-android-with-example)
  - [Custom ArrayAdapter](https://vogella.com/tutorials/AndroidListView/article.html)
  - [Custom ArrayAdapter example code](https://stackoverflow.com/questions/46443776/android-change-textview-font-color-in-a-listview-for-a-condition/46445576#46445576)

Application Toolbar
  - [App Bar](https://developer.android.com/develop/ui/views/components/appbar)
  - [Migrate to AndroidX](https://developer.android.com/jetpack/androidx/migrate)
  - [Using Toolbar](https://github.com/codepath/android_guides/wiki/Using-the-App-ToolBar)

Broadcast Receiver
  - [Broadcast Receiver with example](https://www.geeksforgeeks.org/broadcast-receiver-in-android-with-example)
  - [Battery Monitor](https://developer.android.com/training/monitoring-device-state/battery-monitoring)
  - [System Broadcast Intents](https://developer.android.com/about/versions/11/reference/broadcast-intents-30)

Design
  - [Transparent background color](https://www.tutorialspoint.com/how-to-make-a-background-20-transparent-on-android)
  - [Import Material Icons](https://stackoverflow.com/questions/28684759/import-material-design-icons-into-an-android-project)

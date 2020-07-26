# Developer Tools for Android
__*Developer Tools* is a utility app for Android developers.__

## Download
You can install the app from Play Store at [https://play.google.com/store/apps/details?id=com.roysolberg.android.developertools](https://play.google.com/store/apps/details?id=com.roysolberg.android.developertools&referrer=utm_source%3Dgithub-roys%26utm_medium%3Dreadme%26utm_campaign%3Dreadme).

If you want to download the APK outside Play Store you can [find all releases in the misc/release directory](misc/release).

### Android 2.3.3 - Android 3.2 (API level 10 - 13)
If you want to download the APK outside Play Store for Android from version 2.3.3 to version 3.2 please pick the APK with **version 2.0.8 or earlier** in the [the misc/release directory](misc/release). Later versions will not run on those devices.

### Android 1.5 - Android 2.3.2 (API level 3 - 9)
If you want to download the APK outside Play Store for Android before version 2.3.3 please pick the **version 1 APK** in the [the misc/release directory](misc/release). The version 2 will not run on those devices.

<img src="https://github.com/roys/java-android-developertools/raw/master/misc/screenshots/Screenshot_2016-04-21-23-19-28.png" width="220"/>
<img src="https://github.com/roys/java-android-developertools/raw/master/misc/screenshots/Screenshot_2016-04-21-23-20-07.png" width="220"/>
<img src="https://github.com/roys/java-android-developertools/raw/master/misc/screenshots/Screenshot_2016-04-21-23-20-29.png" width="220"/>

## More details
This app was originally just created for myself to make some development tasks a bit easier. I've released it to the app store hoping that someone else might find it useful too.
Using this app you can see which resource qualifiers that are being used, which system features that are available, and see details about the display and its sizes.

Also I wanted to create an app where everything I needed was just one click away. The full list of features:
- See resource qualifiers in use
- See available system features
- See screen dimensions in dp, pt, in, sp, px, mm
- Start Dalvik Explorer (3rd party app)
- Start aLogcat (3rd party app for viewing logcat)
- Start ManifestViewer (3rd party app for viewing AndroidManifest.xml ++)
- Go directly to the Android 4.3's permission manager (App Ops)
- Go directly to the device's developer settings
- Go directly to the list of installed applications

*Developer Tools* works for Android 1.5 and up (though the version 2.0 here on github currently only supports API level 10+). The app doesn't need any permissions to run.

Please give me a word if there's something you'd like to see included in this app. :)

## Changes
[A change log is included in the project.](app/src/main/assets/CHANGELOG.txt)

## Bugs
Please report any bugs to me. If the app are to crash, please submit a bug report to Play Store as I don't have INTERNET (or any other) permissions and therefore can't use any third party tools to get crash reports and stack traces.

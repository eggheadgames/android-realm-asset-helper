[![Circle CI](https://circleci.com/gh/eggheadgames/android-realm-asset-helper.svg?style=svg)](https://circleci.com/gh/eggheadgames/android-realm-asset-helper)
[![Release](https://jitpack.io/v/eggheadgames/android-realm-asset-helper.svg)](https://jitpack.io/#eggheadgames/android-realm-asset-helper)
<a target="_blank" href="https://android-arsenal.com/api?level=15"><img src="https://img.shields.io/badge/API-15%2B-orange.svg"></a>
[![GitHub license](https://img.shields.io/badge/license-MIT-lightgrey.svg)](https://github.com/eggheadgames/android-realm-asset-helper/blob/master/LICENSE)


# Android Realm Asset Helper

### A small library of methods to help with Realm.IO integration in Android apps

Copies a realm database from a the `assets` folder. Efficienty handles versioning of read-only realm databases.

## About

Motivated by direct support for read-only databases in iOS app bundles and modelled on ideas in [Android SQLite Assset Helper](https://github.com/jgilfelt/android-sqlite-asset-helper), the goal of this library is to help with some common tasks found with using [Realm.io](https://realm.io) in Android. 
It is actively maintained and used by [Egghead Games](http://eggheadgames.com) for their Andriod & iOS brain puzzle apps.

## Features
 - [x] copy the realm db from the app bundle and return filename ready to be opened
 - [x] easy, efficient updating of read-only databases

## Simplest Example

In this simplest scenario, the library handles locating and copying the realm file from your apk into the Android file system so it is ready to use.

Say your app has a single Realm database that ships with some sample data. You need to copy that database on first install so it is ready to use in your app. Store your file called, say, `appdata.realm` in your `assets` folder and include code like this:

```java
import com.eggheadgames.realmassethelper;

Realm realm;

RealmAssetHelper.getInstance(context).loadDatabaseToStorage("appdata", new IRealmAssetHelperStorageListener() {
    @Override
    public void onLoadedToStorage(String realmDbName, RealmAssetHelperStatus status) {
                realmConfig = new RealmConfiguration.Builder(context)
                        .name(realmDbName)
                        .build();
                realm = Realm.getInstance(realmConfig);
            }
    }
});
```
This will:

 * find `appdata.realm` by recursively searching `assets`
 * see if there is an existing `appdata.realm` already installed
 * copy the `appdata.realm` from `assets` only if no pre-existing file is found
 * return the realm file name and status

## Read-only Data Example

Another scenario is where you include a large amount of data to use read-only in your application, such as product catalogue information or game level data.

Say you have the 3rd version of your company's `products.realm` database to ship with your latest apk. 
Store it somewhere in `assets` with the name `products_3.realm`, then load it with:

```java
RealmAssetHelper.getInstance(context).loadDatabaseToStorage("products", new IRealmAssetHelperStorageListener() {
    @Override
    public void onLoadedToStorage(String realmDbName, RealmAssetHelperStatus status) {
                realmConfig = new RealmConfiguration.Builder(context)
                        .name(realmDbName)
                        .build();
                realm = Realm.getInstance(realmConfig);
            }
    }
});
```

This will:

 * find the `products_3.realm` by recursively searching `assets`
 * see if there is an existing `products.realm` installed and check a sharedPreference value to see what version it is
 * copy the realm data if it is newer (removing the `_3`)
 * return the realm file name and status

## Installation Instructions

Add the JitPack.io repository to your root `build.gradle`:

```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

Add a dependency to your application related `build.gradle`

```gradle
dependencies {
    compile 'com.github.eggheadgames:android-realm-asset-helper:1.2.0'
}
```

## Explanation

It can be convenient to have a read-only database as part of an apk. This might have game level information, for example, or other data like zip codes or product information. If the app treats it as "read-only" (perhaps also using a separate realm database for storing other state data), then data updates are as simple as updating the "master" realm file in the apk and then copying it over on first run after the user updates.

iOS note: This is conceptually simpler in iOS, because realm can access read-only data directly from the application bundle (= apk). This library was originally created so that our iOS and Android apps could share the same read-only realm database.

This helper library adds support for this "read-only data included in apk" scenario.

For efficiency, the copy should only be made when the database has changed. This is handled as follows:

 * if no copy of the database exists, it is copied
 * if a copy exists, then a sharedPreference value is checked to see what database version it is (defaults to `0` if not found)
 * the APK `assets` folder is searched for the database name with a postfix `_NN` in the name (e.g. `products_12`). If the `NN` value is higher than the current version, then the new database is copied (with the `_NN` removed) and the sharedPreference value is updated
 * if no database is found in assets, this causes an immediate error (as this is usually an oversight and should be resolved ASAP)

Thus, the workflow for an apk with read-only data becomes:

 * store the database, e.g. `products` in `assets` with the name `products_0`
 * when products is updated, rename the fie from `products_0` to `products_1`

The helper will see the change and copy the database as needed.

## Caveats

There is no consideration given (so far) to database migration requirements or any sort of "update my user's existing realm database from this new realm database". That is clearly a useful enhancement to think about for the future but was beyond the scope of the initial release.

## Pull Requests, Issues, Feedback

We welcome pull requests. If you have questions or bugs, please open an issue and we'll respond promptly.

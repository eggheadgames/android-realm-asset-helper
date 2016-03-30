[![Circle CI](https://circleci.com/gh/eggheadgames/android-realm-asset-helper.svg?style=svg)](https://circleci.com/gh/eggheadgames/android-realm-asset-helper)
[![Release](https://jitpack.io/v/eggheadgames/android-realm-asset-helper.svg)](https://jitpack.io/#eggheadgames/android-realm-asset-helper)

# Android Realm Asset Helper

### A small library of methods to help with Realm.IO integration in Android apps


## About

Modelled on ideas in [Android SQLite Assset Helper](https://github.com/jgilfelt/android-sqlite-asset-helper), the goal of this library is to help with some common tasks found with using [Realm.io](https://realm.io) in Android. 
It is actively maintained and used by [Egghead Games](http://eggheadgames.com).


## Features
 - [x] copy a realm db from an app bundle asset folder so it can be used by Realm
 - [x] handle versioning of a conceptually "read-only" database (copy from app bundle only if necessary)

## Setup

*A quick code sample showing how to use this library will go here.*

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
    compile 'com.github.eggheadgames:android-realm-asset-helper'
}
```


## Status

Work commenced on this library Mar 30, 2016 and is expected to have a first version shortly. 

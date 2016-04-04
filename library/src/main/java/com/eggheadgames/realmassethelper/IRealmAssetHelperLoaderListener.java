package com.eggheadgames.realmassethelper;

import io.realm.Realm;

@SuppressWarnings({"WeakerAccess", "unused"})
public interface IRealmAssetHelperLoaderListener {

    @SuppressWarnings({"EmptyMethod", "UnusedParameters"})
    void onDatabaseLoaded(Realm database, RealmAssetHelperStatus status);
}

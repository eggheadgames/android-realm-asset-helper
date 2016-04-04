package com.eggheadgames.realmassethelper;

import io.realm.Realm;

@SuppressWarnings({"WeakerAccess", "unused"})
public interface IRealmAssetHelperLoaderListener {

    @SuppressWarnings("EmptyMethod")
    void onDatabaseLoaded(@SuppressWarnings("UnusedParameters") Realm database, @SuppressWarnings("UnusedParameters") RealmAssetHelperStatus status);
}

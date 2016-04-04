package com.eggheadgames.realmassethelper;

@SuppressWarnings("WeakerAccess")
public interface IRealmAssetHelperStorageListener {

    @SuppressWarnings("UnusedParameters")
    void onLoadedToStorage(String filePath, RealmAssetHelperStatus status);
}

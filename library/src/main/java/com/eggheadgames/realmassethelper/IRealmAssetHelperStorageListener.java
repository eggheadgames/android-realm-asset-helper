package com.eggheadgames.realmassethelper;

public interface IRealmAssetHelperStorageListener {

    void onLoadedToStorage(String filePath, RealmAssetHelperStatus status);
}

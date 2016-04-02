package com.eggheadgames.realmassethelper;

@SuppressWarnings("WeakerAccess")
public interface IRealmAssetHelperListener {

    void onUpdated();
    void onUpdateIgnored();
    void onFreshInstall();
}

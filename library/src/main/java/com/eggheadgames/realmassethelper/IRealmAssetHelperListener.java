package com.eggheadgames.realmassethelper;

public interface IRealmAssetHelperListener {

    void onUpdated();
    void onUpdateIgnored();
    void onFreshInstall();
}

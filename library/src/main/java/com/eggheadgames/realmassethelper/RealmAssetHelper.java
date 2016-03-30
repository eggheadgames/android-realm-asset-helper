package com.eggheadgames.realmassethelper;

import android.content.Context;

public class RealmAssetHelper {
    protected static RealmAssetHelper instance = new RealmAssetHelper();
    protected Context mContext;
    protected OsUtil mOsUtil;
    private IRealmAssetHelperListener mListener;

    /**
     *
     * Please consider using Application Context as a @param context
     */
    public static RealmAssetHelper getInstance(Context context) {
        instance.mContext = context;
        instance.mOsUtil = new OsUtil();
        return instance;
    }

    protected RealmAssetHelper() {
    }

    public void loadDatabaseToStorage(String databaseName) throws RuntimeException {

    }

    public void setListener (IRealmAssetHelperListener listener) {
        mListener = listener;
    }
}

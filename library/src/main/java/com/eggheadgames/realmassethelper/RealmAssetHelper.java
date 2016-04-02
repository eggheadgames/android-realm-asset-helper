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
        mOsUtil.clearCache();

        if (mOsUtil.isEmpty(databaseName)) {
            throw new RuntimeException("The database name is empty");
        }

        if (!mOsUtil.isDatabaseAssetExists(mContext, databaseName)) {
            throw new RuntimeException("An asset for requested database doesn't exist");
        }

        Integer currentDbVersion = mOsUtil.getCurrentDbVersion(mContext, databaseName);
        int assetsDbVersion = mOsUtil.getAssetsDbVersion(mContext, databaseName);

        //fresh install
        if (currentDbVersion == null) {
            mOsUtil.loadDatabaseToLocalStorage(mContext, databaseName);
            mOsUtil.storeDatabaseVersion(mContext, assetsDbVersion, databaseName);
            if (mListener != null) {
                mListener.onFreshInstall();
            }
        }

    }

    public void setListener (IRealmAssetHelperListener listener) {
        mListener = listener;
    }
}

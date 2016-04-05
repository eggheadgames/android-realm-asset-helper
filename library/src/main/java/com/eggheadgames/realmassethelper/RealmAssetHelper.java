package com.eggheadgames.realmassethelper;

import android.content.Context;

public class RealmAssetHelper {
    @SuppressWarnings({"WeakerAccess", "CanBeFinal"})
    protected static RealmAssetHelper instance = new RealmAssetHelper();
    @SuppressWarnings("WeakerAccess")
    protected Context mContext;
    @SuppressWarnings("WeakerAccess")
    protected OsUtil mOsUtil;

    /**
     *
     * Please consider using Application Context as a @param context
     */
    @SuppressWarnings("unused")
    public static RealmAssetHelper getInstance(Context context) {
        instance.mContext = context;
        instance.mOsUtil = new OsUtil();
        return instance;
    }

    @SuppressWarnings("WeakerAccess")
    protected RealmAssetHelper() {
    }

    /**
     * Loads an asset to the file system.
     * Path to the file will be returned via a callback
     *
     * P.S. The file will be stored by the following path:
     * context.getFilesDir() + File.separator + databaseName + ".realm"
     *
     * @param databaseName a database name without version and file extension.
     *                     e.g. if you have an asset file data/testdatabase_15.realm
     *                     then you should specify testdatabase as a databaseName
     * @param listener will notify about the status and return an instance of Realm database if there is no error
     * @throws RuntimeException in case if specified databaseName is empty,
     * or assets with specified name not found,
     * or file was not written to the filesystem
     */
    public void loadDatabaseToStorage(String databaseName, IRealmAssetHelperStorageListener listener) throws RuntimeException {
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
            String path = mOsUtil.loadDatabaseToLocalStorage(mContext, databaseName);
            if (mOsUtil.isEmpty(path)) {
                throw new RuntimeException("Can't find copied file");
            }
            mOsUtil.storeDatabaseVersion(mContext, assetsDbVersion, databaseName);
            if (listener != null) {
                listener.onLoadedToStorage(path, RealmAssetHelperStatus.INSTALLED);
            }
        } else {
            //update required
            if (assetsDbVersion > currentDbVersion) {
                String path = mOsUtil.loadDatabaseToLocalStorage(mContext, databaseName);
                if (mOsUtil.isEmpty(path)) {
                    throw new RuntimeException("Can't find copied file");
                }
                mOsUtil.storeDatabaseVersion(mContext, assetsDbVersion, databaseName);
                if (listener != null) {
                    listener.onLoadedToStorage(path, RealmAssetHelperStatus.UPDATED);
                }
                //do not update
            } else {

                String path = mOsUtil.getFileNameForDatabase(mContext, databaseName);
                if (mOsUtil.isEmpty(path)) {
                    throw new RuntimeException("Can't find copied file");
                }
                if (listener != null) {
                    listener.onLoadedToStorage(path, RealmAssetHelperStatus.IGNORED);
                }
            }
        }
    }
}

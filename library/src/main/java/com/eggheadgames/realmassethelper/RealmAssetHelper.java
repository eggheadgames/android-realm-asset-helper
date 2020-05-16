package com.eggheadgames.realmassethelper;

import android.annotation.SuppressLint;
import android.content.Context;

public class RealmAssetHelper {
    @SuppressLint("StaticFieldLeak")
    protected static final RealmAssetHelper instance = new RealmAssetHelper();
    protected Context mContext;
    protected OsUtil mOsUtil;

    /**
     * Please consider using Application Context as a @param context
     */
    public static RealmAssetHelper getInstance(Context context) {
        Context applicationContext = context.getApplicationContext();
        instance.mContext = applicationContext == null ? context : applicationContext;
        instance.mOsUtil = new OsUtil();
        return instance;
    }

    protected RealmAssetHelper() {
    }

    /**
     * Loads an asset to the file system.
     * Path to the file will be returned via a callback
     * <p>
     * P.S. The file will be stored by the following path:
     * context.getFilesDir() + File.separator + databaseName + ".realm"
     *
     * @param databaseName   a database name without version and file extension.
     *                       e.g. if you have an asset file data/testdatabase_15.realm
     *                       then you should specify testdatabase as a databaseName
     * @param databaseFolder name of folder where database is located
     * @param listener       will notify about the status and return an instance of Realm database if there is no error
     * @throws RuntimeException in case if specified databaseName is empty,
     *                          or assets with specified name not found,
     *                          or file was not written to the filesystem
     */
    public void loadDatabaseToStorage(String databaseFolder, String databaseName, IRealmAssetHelperStorageListener listener) throws RuntimeException {
        mOsUtil.clearCache();

        if (mOsUtil.isEmpty(databaseName)) {
            throw new RuntimeException("The database name is empty");
        }

        if (!mOsUtil.isDatabaseAssetExists(mContext, databaseFolder, databaseName)) {
            throw new RuntimeException("An asset for requested database doesn't exist");
        }

        Integer currentDbVersion = mOsUtil.getCurrentDbVersion(mContext, databaseName);
        int assetsDbVersion = mOsUtil.getAssetsDbVersion(mContext, databaseFolder, databaseName);

        //fresh install
        if (currentDbVersion == null) {
            String path = mOsUtil.loadDatabaseToLocalStorage(mContext, databaseFolder, databaseName);
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
                String path = mOsUtil.loadDatabaseToLocalStorage(mContext, databaseFolder, databaseName);
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

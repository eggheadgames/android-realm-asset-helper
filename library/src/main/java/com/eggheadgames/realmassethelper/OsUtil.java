package com.eggheadgames.realmassethelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.Preferences;

public class OsUtil {

    private String cachedAssetPath;

    public void loadDatabaseToLocalStorage(Context context, String databaseName) {
        String asset = findAsset(context, "", databaseName);

        File f = new File(context.getFilesDir() + File.separator + databaseName);
        if (f.exists()) {
            f.delete();
        }
        try {
            InputStream is = context.getAssets().open(asset);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Integer getCurrentDbVersion(Context context, String databaseName) {
        return null;
    }

    public int getAssetsDbVersion(Context context, String databaseName) {
        return 0;
    }

    public void storeDatabaseVersion(Context context, int version, String databaseName) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(Constants.PREFERENCES_DB_VERSION + databaseName, version);
    }

    public boolean isEmpty(String string) {
        return TextUtils.isEmpty(string);
    }

    /**
     * expected asset name <databaseName>_xx.realm or <databaseName>.realm
     */
    public boolean isDatabaseAssetExists(Context context, String databaseName) {
        return !TextUtils.isEmpty(findAsset(context, "", databaseName));
    }

    public void clearCache() {
        cachedAssetPath = null;
    }

    private String findAsset(Context context, String path, String databaseName) {
        if (!TextUtils.isEmpty(cachedAssetPath)) {
            return cachedAssetPath;
        } else {
            try {
                String[] list;
                list = context.getAssets().list(path);
                if (list.length > 0) {
                    for (String file : list) {
                        String asset = findAsset(context,
                                TextUtils.isEmpty(path) ? file : path + File.separator + file,
                                databaseName);
                        if (!TextUtils.isEmpty(asset)) {
                            return asset;
                        }
                    }
                } else {
                    //it's a file
                    String fileName = new File(path).getName();
                    if (!TextUtils.isEmpty(fileName)) {
                        if (fileName.matches(databaseName + "_\\d+\\.realm") || fileName.matches(databaseName + ".realm")) {
                            cachedAssetPath = path;
                            return path;
                        }
                    }
                }
            } catch (IOException e) {
                return null;
            }
            return null;
        }
    }
}

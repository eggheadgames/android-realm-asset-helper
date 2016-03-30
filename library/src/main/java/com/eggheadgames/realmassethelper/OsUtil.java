package com.eggheadgames.realmassethelper;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class OsUtil {

    public void loadDatabaseToLocalStorage(Context context, String databaseName) {
        File f = new File(context.getFilesDir() + File.separator + databaseName);
        if (!f.exists()) {
            try {
                InputStream is = context.getAssets().open(databaseName);
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
    }

    public Integer getCurrentDbVersion(Context context) {
        return null;
    }

    public int getAssetsDbVersion(Context context) {
        return 0;
    }

    public void storeDatabaseVersion(int version) {

    }
}

package com.roysolberg.android.developertools.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class Utils {

    public static String getVersion(final Context context) {
        PackageManager packageManager = context.getPackageManager();
        String version = "";
        if (packageManager != null) {
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), -1);
                version = packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                /* no-op */
            }
        }
        return version;
    }

}

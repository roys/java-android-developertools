package com.roysolberg.android.developertools.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

public class Utils {

    public static String getVersion(final Context context) {
        PackageManager packageManager = context.getPackageManager();
        String version = "";
        if (packageManager != null) {
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                version = packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                /* no-op */
            } catch (SecurityException e) {
                /* no-op */
            }
        }
        return version;
    }

    public static String getDeviceSummary() {
        return Build.MANUFACTURER + " " + Build.MODEL + " (" + Build.VERSION.RELEASE + "/" + Build.VERSION.SDK + "/" + Build.ID + ")";
    }

}

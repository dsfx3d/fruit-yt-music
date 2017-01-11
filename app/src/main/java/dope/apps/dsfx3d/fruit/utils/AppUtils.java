package dope.apps.dsfx3d.fruit.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import static android.R.attr.versionName;

/**
 * Created by dsfx3d on 4/1/17.
 */

public class AppUtils {

    public static int getVersionCode(Context context) {
        int versionCode = -1;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return  versionCode;
    }
}

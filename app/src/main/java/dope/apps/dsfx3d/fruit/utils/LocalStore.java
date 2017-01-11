package dope.apps.dsfx3d.fruit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 */

public class LocalStore {
    private SharedPreferences store;
    private SharedPreferences.Editor editor;
    private Context context;

    private LocalStore(Context context) {
        this.context = context;
        store = PreferenceManager.getDefaultSharedPreferences(context);
        store.edit();
    }

    public static LocalStore open(Context context) {
        return new LocalStore(context);
    }

    public void putString(String key, String val) {
        editor.putString(key,val);
        editor.apply();
    }

    public String getString(String key, String defValue) {
        return store.getString(key,defValue);
    }
}

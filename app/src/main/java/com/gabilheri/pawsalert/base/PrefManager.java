package com.gabilheri.pawsalert.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;
import java.util.Set;

/**
 * Created by <a href="mailto:marcusandreog@gmail.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/17/16.
 */
public final class PrefManager {

    static PrefManager singleton = null;

    static SharedPreferences preferences;

    static SharedPreferences.Editor editor;

    private PrefManager() { }

    PrefManager(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    PrefManager(Context context, String name, int mode) {
        preferences = context.getSharedPreferences(name, mode);
        editor = preferences.edit();
    }

    public static PrefManager with(Context context) {
        if (singleton == null) {
            singleton = new Builder(context, null, -1).build();
        }
        return singleton;
    }

    public static PrefManager with(Context context, String name, int mode) {
        if (singleton == null) {
            singleton = new Builder(context, name, mode).build();
        }
        return singleton;
    }

    public void save(String key, boolean value) {
        editor.putBoolean(key, value).apply();
    }

    public void save(String key, String value) {
        editor.putString(key, value).apply();
    }

    public void save(String key, int value) {
        editor.putInt(key, value).apply();
    }

    public void save(String key, float value) {
        editor.putFloat(key, value).apply();
    }

    public void save(String key, long value) {
        editor.putLong(key, value).apply();
    }

    public void save(String key, Set<String> value) {
        editor.putStringSet(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return preferences.getBoolean(key, defValue);
    }

    public String getString(String key, String defValue) {
        return preferences.getString(key, defValue);
    }

    public int getInt(String key, int defValue) {
        try {
            return preferences.getInt(key, defValue);
        } catch (ClassCastException ex) {
            return Integer.parseInt(preferences.getString(key, String.valueOf(defValue)));
        }

    }

    public float getFloat(String key, float defValue) {
        try  {
            return preferences.getFloat(key, defValue);
        } catch (ClassCastException ex) {
            return Float.parseFloat(preferences.getString(key, String.valueOf(defValue)));
        }
    }

    public long getLong(String key, long defValue) {
        try {
            return preferences.getLong(key, defValue);
        } catch (ClassCastException ex) {
            return Long.parseLong(preferences.getString(key, String.valueOf(defValue)));
        }
    }

    public Set<String> getStringSet(String key, Set<String> defValue) {
        return preferences.getStringSet(key, defValue);
    }

    public Map<String, ?> getAll() {
        return preferences.getAll();
    }

    public void remove(String key) {
        editor.remove(key).apply();
    }

    public void clear() {
        editor.clear().apply();
    }

    public void registerSharedPreferenceListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterSharedPreferenceListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    private static class Builder {

        private final Context context;
        private final int mode;
        private final String name;

        public Builder(Context context, String name, int mode) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context.getApplicationContext();
            this.name = name;
            this.mode = mode;
        }

        /**
         * Method that creates an instance of PrefManager
         *
         * @return an instance of PrefManager
         */
        public PrefManager build() {
            if (mode == -1 || name == null) {
                return new PrefManager(context);
            }
            return new PrefManager(context, name, mode);
        }
    }
}

package com.example.prefixblocker.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PrefsManager {
    private static final String PREF_NAME = "prefix_blocker_prefs";
    private static final String KEY_PREFIXES = "prefixes";
    private static final String KEY_STATISTICS = "statistics";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_THEME = "theme";

    private SharedPreferences prefs;
    private Gson gson;

    public PrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void savePrefixes(List<Prefix> prefixes) {
        String json = gson.toJson(prefixes);
        prefs.edit().putString(KEY_PREFIXES, json).apply();
    }

    public List<Prefix> getPrefixes() {
        String json = prefs.getString(KEY_PREFIXES, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Prefix>>(){}.getType();
        List<Prefix> result = gson.fromJson(json, type);
        return result != null ? result : new ArrayList<>();
    }

    public void addPrefix(String prefix) {
        List<Prefix> list = getPrefixes();
        for (Prefix p : list) {
            if (p.getPrefix().equals(prefix)) {
                return;
            }
        }
        list.add(new Prefix(prefix, true));
        savePrefixes(list);
    }

    public void removePrefix(String prefix) {
        List<Prefix> list = getPrefixes();
        list.removeIf(p -> p.getPrefix().equals(prefix));
        savePrefixes(list);
    }

    public void togglePrefix(String prefix) {
        List<Prefix> list = getPrefixes();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPrefix().equals(prefix)) {
                Prefix p = list.get(i);
                p.setActive(!p.isActive());
                break;
            }
        }
        savePrefixes(list);
    }

    public List<String> getActivePrefixes() {
        List<String> active = new ArrayList<>();
        for (Prefix p : getPrefixes()) {
            if (p.isActive()) {
                active.add(p.getPrefix());
            }
        }
        return active;
    }

    public void addBlockedCall(BlockedCall call) {
        List<BlockedCall> list = getStatistics();
        list.add(0, call);
        if (list.size() > 100) {
            list = list.subList(0, 100);
        }
        String json = gson.toJson(list);
        prefs.edit().putString(KEY_STATISTICS, json).apply();
    }

    public List<BlockedCall> getStatistics() {
        String json = prefs.getString(KEY_STATISTICS, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<BlockedCall>>(){}.getType();
        List<BlockedCall> result = gson.fromJson(json, type);
        return result != null ? result : new ArrayList<>();
    }

    public void clearStatistics() {
        prefs.edit().remove(KEY_STATISTICS).apply();
    }

    public int getBlockedCount() {
        return getStatistics().size();
    }

    public boolean isFirstLaunch() {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public void setFirstLaunchDone() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
    }

    public void saveTheme(int themeMode) {
        prefs.edit().putInt(KEY_THEME, themeMode).apply();
    }

    public int getTheme() {
        return prefs.getInt(KEY_THEME, 0);
    }
}
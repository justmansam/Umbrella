package com.example.umbrella.data.local.pref

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPreferencesImpl @Inject constructor(
    private val pref: SharedPreferences
) : ISharedPreferences {

    override fun setValue(key: String, value: String) {
        pref.edit().putString(key, value).apply()
    }

    override fun removeValue(key: String) {
        pref.edit().remove(key).apply()
    }

    override fun getValue(key: String): String? {
        return pref.getString(key, null)
    }
}

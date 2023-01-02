package com.example.umbrella.data.local.pref

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPreferencesImpl @Inject constructor(
    private val pref: SharedPreferences
) : ISharedPreferences {

    override suspend fun setValue(key: Array<String>, value: Array<String>) {
        for (i in key) {
            pref.edit().putString(i, value[key.indexOf(i)]).apply()
        }
    }

    override suspend fun removeValue(key: Array<String>) {
        for (i in key) {
            pref.edit().remove(i).apply()
        }
    }

    override suspend fun getValue(key: Array<String>): Array<String?> {
        var arrayOfSharedPref: Array<String?> = emptyArray()
        for (i in key) {
            arrayOfSharedPref += pref.getString(i, null)
        }
        return arrayOfSharedPref
    }
}

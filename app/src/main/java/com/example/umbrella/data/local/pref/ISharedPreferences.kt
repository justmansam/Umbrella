package com.example.umbrella.data.local.pref

interface ISharedPreferences {
    suspend fun setSharedPref(key: Array<String>, value: Array<String?>)
    suspend fun getSharedPref(key: Array<String>): Array<String?>
    suspend fun removeSharedPref(key: Array<String>)
}
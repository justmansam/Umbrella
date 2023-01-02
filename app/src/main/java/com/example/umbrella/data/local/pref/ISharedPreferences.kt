package com.example.umbrella.data.local.pref

interface ISharedPreferences {
    suspend fun setValue(key: Array<String>, value: Array<String>)
    suspend fun getValue(key: Array<String>): Array<String?>
    suspend fun removeValue(key: Array<String>)
}
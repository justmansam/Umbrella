package com.example.umbrella.pref

interface ISharedPreferences {
    fun setValue(key: String, value: String)
    fun getValue(key: String): String?
    fun removeValue(key: String)
}
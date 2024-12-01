package com.dev.sk.xchangehub.data.local.sharedpref

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

const val TIMESTAMP = "TIMESTAMP"

interface SharedPreferenceService {
    fun putLong(key: String, value: Long)
    fun getLong(key: String, defaultValue: Long): Long
}

@Singleton
class DefaultSharedPreferenceService @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SharedPreferenceService {

    override fun putLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }
}
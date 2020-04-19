package com.gurtam.wiatag_kit.example

import android.content.Context
import android.content.SharedPreferences

object Utils {
    private const val PREFERENCES_KEY = "messages_preferences_key"

    fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
    }
}
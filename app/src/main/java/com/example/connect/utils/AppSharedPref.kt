package com.example.connect.utils

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class AppSharedPref @Inject constructor(val context: Context) {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPref.edit()

    companion object {
        const val SHARED_PREF_NAME = "com.example.connect.shared_pref"
        const val IS_USER_INFO_ADDED = "is_user_info_added"
    }

    var isUserInfoAdded: Boolean
        get() = sharedPref.getBoolean(IS_USER_INFO_ADDED, false)
        set(value) {
            editor.putBoolean(IS_USER_INFO_ADDED, value)
            editor.apply()
        }
}
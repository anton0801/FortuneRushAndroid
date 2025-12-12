package com.appslocraapp.slotscrashapp.ui.ie.handler

import android.content.Context
import androidx.core.content.edit

class FortuneRushLocalStorageManager(context: Context) {
    private val feedMixPrefs =
        context.getSharedPreferences("feedMixsharedPrefsAb", Context.MODE_PRIVATE)

    var feedMixNotificationRequestedBefore: Boolean
        get() = feedMixPrefs.getBoolean(FEED_MIX_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = feedMixPrefs.edit {
            putBoolean(
                FEED_MIX_NOTIFICATION_REQUEST_BEFORE, value
            )
        }
    var feedMixExpired: Long
        get() = feedMixPrefs.getLong(FEED_MIX_EXPIRED, 0L)
        set(value) = feedMixPrefs.edit { putLong(FEED_MIX_EXPIRED, value) }

    var feedMixAppState: Int
        get() = feedMixPrefs.getInt(FEED_MIX_APPLICATION_STATE, 0)
        set(value) = feedMixPrefs.edit { putInt(FEED_MIX_APPLICATION_STATE, value) }

    var feedMixSavedUrl: String
        get() = feedMixPrefs.getString(FEED_MIX_SAVED_URL, "") ?: ""
        set(value) = feedMixPrefs.edit { putString(FEED_MIX_SAVED_URL, value) }

    var feedMixNotificationRequest: Long
        get() = feedMixPrefs.getLong(FEED_MIX_NOTIFICAITON_REQUEST, 0L)
        set(value) = feedMixPrefs.edit { putLong(FEED_MIX_NOTIFICAITON_REQUEST, value) }


    companion object {
        private const val FEED_MIX_SAVED_URL = "eggLabelSavedUrl"
        private const val FEED_MIX_EXPIRED = "eggLabelExpired"
        private const val FEED_MIX_APPLICATION_STATE = "eggLabelApplicationState"
        private const val FEED_MIX_NOTIFICAITON_REQUEST = "eggLabelNotificationRequest"
        private const val FEED_MIX_NOTIFICATION_REQUEST_BEFORE =
            "eggLabelNotificationRequestedBefore"
    }
}
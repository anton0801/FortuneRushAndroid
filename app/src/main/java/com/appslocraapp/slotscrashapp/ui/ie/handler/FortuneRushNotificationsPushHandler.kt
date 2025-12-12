package com.appslocraapp.slotscrashapp.ui.ie.handler

import android.os.Bundle
import android.util.Log
import com.appslocraapp.slotscrashapp.app.MainApplication

class FortuneRushNotificationsPushHandler {

    fun feedMixAppHandlePush(extras: Bundle?) {
        Log.d(MainApplication.FORTUNE_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = chickHealthBundleToMap(extras)
            Log.d(MainApplication.FORTUNE_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    MainApplication.FEED_MIX_FB_LI = map["url"]
                    Log.d(MainApplication.FORTUNE_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(MainApplication.FORTUNE_MAIN_TAG, "Push data no!")
        }
    }

    private fun chickHealthBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}
package com.appslocraapp.slotscrashapp.ui.ie.domain

import android.util.Log
import com.appslocraapp.slotscrashapp.app.MainApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FortuneRushPushTokenUseCase {

    suspend fun fortRuGetToken(): String = suspendCoroutine { continuation ->
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (!it.isSuccessful) {
                    continuation.resume(it.result)
                    Log.d(MainApplication.FORTUNE_MAIN_TAG, "Token error: ${it.exception}")
                } else {
                    continuation.resume(it.result)
                }
            }
        } catch (e: Exception) {
            Log.d(MainApplication.FORTUNE_MAIN_TAG, "FirebaseMessagingPushToken = null")
            continuation.resume("")
        }
    }


}
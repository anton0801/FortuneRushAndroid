package com.appslocraapp.slotscrashapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import com.appslocraapp.slotscrashapp.app.MainApplication
import com.appslocraapp.slotscrashapp.ui.ie.handler.FortuneRushGlobalLayoutUtils
import com.appslocraapp.slotscrashapp.ui.ie.handler.FortuneRushNotificationsPushHandler
import com.appslocraapp.slotscrashapp.ui.ie.handler.FortuneRushSetupSystemBars
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import kotlin.getValue

class StartActivity : AppCompatActivity() {

    private val feedMixPushHandler by inject<FortuneRushNotificationsPushHandler>()

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            FortuneRushSetupSystemBars()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FortuneRushSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_start)

        val feedMixRootView = findViewById<View>(android.R.id.content)
        FortuneRushGlobalLayoutUtils().feedMixAssistActivity(this)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        ViewCompat.setOnApplyWindowInsetsListener(feedMixRootView) { feedMixView, feedMixInsets ->
            val feedMixSystemBars = feedMixInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val feedMixDisplayCutout =
                feedMixInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val feedMixIme = feedMixInsets.getInsets(WindowInsetsCompat.Type.ime())
            val feedMixTopPadding = maxOf(feedMixSystemBars.top, feedMixDisplayCutout.top)
            val feedMixLeftPadding = maxOf(feedMixSystemBars.left, feedMixDisplayCutout.left)
            val feedMixRightPadding = maxOf(feedMixSystemBars.right, feedMixDisplayCutout.right)
            window.setSoftInputMode(MainApplication.feedMInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                val feedMixBottomInset =
                    maxOf(feedMixSystemBars.bottom, feedMixDisplayCutout.bottom)
                feedMixView.setPadding(
                    feedMixLeftPadding,
                    feedMixTopPadding,
                    feedMixRightPadding,
                    0
                )
                feedMixView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = feedMixBottomInset
                }
            } else {
                val feedMixBottomInset =
                    maxOf(feedMixSystemBars.bottom, feedMixDisplayCutout.bottom, feedMixIme.bottom)
                feedMixView.setPadding(
                    feedMixLeftPadding,
                    feedMixTopPadding,
                    feedMixRightPadding,
                    0
                )
                feedMixView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = feedMixBottomInset
                }
            }
            WindowInsetsCompat.CONSUMED
        }
        feedMixPushHandler.feedMixAppHandlePush(intent.extras)

        lifecycleScope.launch {
            feedMixRetriveDeviceGaid()
        }
    }

    suspend fun feedMixRetriveDeviceGaid(): String = withContext(Dispatchers.IO) {
        val gaid = AdvertisingIdClient.getAdvertisingIdInfo(this@StartActivity).id
            ?: "00000000-0000-0000-0000-000000000000"
        Log.d(MainApplication.FORTUNE_MAIN_TAG, "Gaid: $gaid")
        return@withContext gaid
    }

    override fun onResume() {
        super.onResume()
        FortuneRushSetupSystemBars()
    }

}
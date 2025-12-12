package com.appslocraapp.slotscrashapp.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkResult
import com.appslocraapp.slotscrashapp.ui.ie.domain.FortuneRushChSystemServiceI
import com.appslocraapp.slotscrashapp.ui.ie.domain.FortuneRushGetAllUseCaseInApp
import com.appslocraapp.slotscrashapp.ui.ie.domain.FortuneRushPushTokenUseCase
import com.appslocraapp.slotscrashapp.ui.ie.domain.FortuneRushRepositoryImpl
import com.appslocraapp.slotscrashapp.ui.ie.handler.FortuneRushLocalStorageManager
import com.appslocraapp.slotscrashapp.ui.ie.handler.FortuneRushNotificationsPushHandler
import com.appslocraapp.slotscrashapp.ui.ie.presentation.util.ForrrttRushhhhhViFun
import com.appslocraapp.slotscrashapp.ui.ie.presentation.FortRushhhLoadViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface FeedMixAppsFlyerState {
    data object FeedMixDefault : FeedMixAppsFlyerState
    data class FeedMixSuccess(val feedMixxChickkData: MutableMap<String, Any>?) :
        FeedMixAppsFlyerState

    data object FeedMixError : FeedMixAppsFlyerState
}

class MainApplication : Application() {

    private var feedMixChIsResumed = false
    private var feedMIxConvTimeoutJob: Job? = null
    private var feedMixxxChickkDeepLinksMap: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        feedMixDebugLoggerMode(appsflyer)
        feedMix(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink { p0 ->
            when (p0.status) {
                DeepLinkResult.Status.FOUND -> {
                    feedMixDDExtractDeepLinksData(p0.deepLink)
                    Log.d(FORTUNE_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                }

                DeepLinkResult.Status.NOT_FOUND -> {
                    Log.d(FORTUNE_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                }

                DeepLinkResult.Status.ERROR -> {
                    Log.d(FORTUNE_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                }
            }
        }

        appsflyer.init(
            FEED_MIX_APPSFLYER_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    feedMIxConvTimeoutJob?.cancel()
                    Log.d(FORTUNE_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = feedMixGetApiMethodsForAppsflyer(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.eggLabelGetClient(
                                    devkey = FEED_MIX_APPSFLYER_DEV,
                                    deviceId = feedMixGetAppserId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(FORTUNE_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic") {
                                    feedMixChickResume(FeedMixAppsFlyerState.FeedMixError)
                                } else {
                                    feedMixChickResume(
                                        FeedMixAppsFlyerState.FeedMixSuccess(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(FORTUNE_MAIN_TAG, "Error: ${d.message}")
                                feedMixChickResume(FeedMixAppsFlyerState.FeedMixError)
                            }
                        }
                    } else {
                        feedMixChickResume(FeedMixAppsFlyerState.FeedMixSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    feedMIxConvTimeoutJob?.cancel()
                    Log.d(FORTUNE_MAIN_TAG, "onConversionDataFail: $p0")
                    feedMixChickResume(FeedMixAppsFlyerState.FeedMixError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(FORTUNE_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(FORTUNE_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, FEED_MIX_APPSFLYER_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(FORTUNE_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(FORTUNE_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
                feedMixChickResume(FeedMixAppsFlyerState.FeedMixError)
            }
        })
        feedMixStartConvTimeot()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MainApplication)
            modules(
                listOf(
                    feedMixModule
                )
            )
        }
    }

    private fun feedMixDebugLoggerMode(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun feedMixDDExtractDeepLinksData(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(FORTUNE_MAIN_TAG, "Extracted DeepLink data: $map")
        feedMixxxChickkDeepLinksMap = map
    }

    private fun feedMixGetAppserId(): String =
        AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""


    companion object {
        var feedMInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val feedMMConversionFlow: MutableStateFlow<FeedMixAppsFlyerState> = MutableStateFlow(
            FeedMixAppsFlyerState.FeedMixDefault
        )
        var FEED_MIX_FB_LI: String? = null
        const val FORTUNE_MAIN_TAG = "FEEDMIX_MainTag"
    }

    private fun feedMixGetApiMethodsForAppsflyer(
        url: String,
        client: OkHttpClient?
    ): FeedMixAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    private fun feedMixStartConvTimeot() {
        feedMIxConvTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!feedMixChIsResumed) {
                Log.d(FORTUNE_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                feedMixChickResume(FeedMixAppsFlyerState.FeedMixError)
            }
        }
    }

    private fun feedMixChickResume(state: FeedMixAppsFlyerState) {
        feedMIxConvTimeoutJob?.cancel()
        if (state is FeedMixAppsFlyerState.FeedMixSuccess) {
            val convData = state.feedMixxChickkData ?: mutableMapOf()
            val deepData = feedMixxxChickkDeepLinksMap ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!feedMixChIsResumed) {
                feedMixChIsResumed = true
                feedMMConversionFlow.value = FeedMixAppsFlyerState.FeedMixSuccess(merged)
            }
        } else {
            if (!feedMixChIsResumed) {
                feedMixChIsResumed = true
                feedMMConversionFlow.value = state
            }
        }
    }

    private fun feedMix(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }
}

interface FeedMixAppsApi {
    @Headers("Content-Type: application/json")
    @GET(FEED_MIX_LIN)
    fun eggLabelGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val FEED_MIX_APPSFLYER_DEV = "ZXSmKKurzrCDdVdVCP9N4R"
private const val FEED_MIX_LIN = "com.appslocraapp.slotscrashapp"

val feedMixModule = module {
    factory {
        FortuneRushNotificationsPushHandler()
    }
    single {
        FortuneRushRepositoryImpl()
    }
    single {
        FortuneRushLocalStorageManager(get())
    }
    factory {
        FortuneRushPushTokenUseCase()
    }
    factory {
        FortuneRushChSystemServiceI(get())
    }
    factory {
        FortuneRushGetAllUseCaseInApp(
            get(), get(), get()
        )
    }
    factory {
        ForrrttRushhhhhViFun(get())
    }
    viewModel {
        FortRushhhLoadViewModel(get(), get(), get())
    }
}
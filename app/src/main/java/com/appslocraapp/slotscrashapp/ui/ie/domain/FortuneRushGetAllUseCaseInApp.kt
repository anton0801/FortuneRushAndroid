package com.appslocraapp.slotscrashapp.ui.ie.domain

import android.util.Log
import com.appslocraapp.slotscrashapp.app.MainApplication

class FortuneRushGetAllUseCaseInApp(
    private val fortuneRushRepositoryImpl: FortuneRushRepositoryImpl,
    private val fortuneRushChSystemServiceI: FortuneRushChSystemServiceI,
    private val fortuneRushPushTokenUseCase: FortuneRushPushTokenUseCase,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?): FortuneRushEntity? {
        val params = FortuneRushParam(
            fortuneRushLocale = fortuneRushChSystemServiceI.getLocaleOfUserFeedMix(),
            fortuneRushPushToken = fortuneRushPushTokenUseCase.fortRuGetToken(),
            fortuneRushAfId = fortuneRushChSystemServiceI.getAppsflyerIdForApp()
        )
        Log.d(MainApplication.FORTUNE_MAIN_TAG, "Params for request: $params")
        return fortuneRushRepositoryImpl.feedMixAppGetClient(params, conversion)
    }


}
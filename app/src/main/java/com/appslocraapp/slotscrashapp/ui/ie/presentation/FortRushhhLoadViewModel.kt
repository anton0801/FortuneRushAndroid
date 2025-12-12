package com.appslocraapp.slotscrashapp.ui.ie.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appslocraapp.slotscrashapp.app.FeedMixAppsFlyerState
import com.appslocraapp.slotscrashapp.app.MainApplication
import com.appslocraapp.slotscrashapp.ui.ie.domain.FortuneRushChSystemServiceI
import com.appslocraapp.slotscrashapp.ui.ie.domain.FortuneRushGetAllUseCaseInApp
import com.appslocraapp.slotscrashapp.ui.ie.handler.FortuneRushLocalStorageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FortRushhhLoadViewModel(
    private val fortuneRushGetAllUseCaseInApp: FortuneRushGetAllUseCaseInApp,
    private val fortuneRushLocalStorageManager: FortuneRushLocalStorageManager,
    private val fortuneRushChSystemServiceI: FortuneRushChSystemServiceI
) : ViewModel() {

    private val _chickHealthHomeScreenState: MutableStateFlow<FeedMixHomeScreenState> =
        MutableStateFlow(FeedMixHomeScreenState.FeedMixLoading)
    val chickHealthHomeScreenState = _chickHealthHomeScreenState.asStateFlow()

    private var eggLabelGetApps = false

    init {
        viewModelScope.launch {
            when (fortuneRushLocalStorageManager.feedMixAppState) {
                0 -> {
                    if (fortuneRushChSystemServiceI.feedMixCheckInternetConnection()) {
                        MainApplication.feedMMConversionFlow.collect {
                            when (it) {
                                FeedMixAppsFlyerState.FeedMixDefault -> {}
                                FeedMixAppsFlyerState.FeedMixError -> {
                                    fortuneRushLocalStorageManager.feedMixAppState = 2
                                    _chickHealthHomeScreenState.value =
                                        FeedMixHomeScreenState.FeedMixError
                                    eggLabelGetApps = true
                                }

                                is FeedMixAppsFlyerState.FeedMixSuccess -> {
                                    if (!eggLabelGetApps) {
                                        feedMixGetData(it.feedMixxChickkData)
                                        eggLabelGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _chickHealthHomeScreenState.value =
                            FeedMixHomeScreenState.FeedMixNotInternet
                    }
                }

                1 -> {
                    if (fortuneRushChSystemServiceI.feedMixCheckInternetConnection()) {
                        if (MainApplication.FEED_MIX_FB_LI != null) {
                            _chickHealthHomeScreenState.value =
                                FeedMixHomeScreenState.FeedMixSuccess(
                                    MainApplication.FEED_MIX_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > fortuneRushLocalStorageManager.feedMixExpired) {
                            Log.d(
                                MainApplication.FORTUNE_MAIN_TAG,
                                "Current time more then expired, repeat request"
                            )
                            MainApplication.feedMMConversionFlow.collect {
                                when (it) {
                                    FeedMixAppsFlyerState.FeedMixDefault -> {}
                                    FeedMixAppsFlyerState.FeedMixError -> {
                                        _chickHealthHomeScreenState.value =
                                            FeedMixHomeScreenState.FeedMixSuccess(
                                                fortuneRushLocalStorageManager.feedMixSavedUrl
                                            )
                                        eggLabelGetApps = true
                                    }

                                    is FeedMixAppsFlyerState.FeedMixSuccess -> {
                                        if (!eggLabelGetApps) {
                                            feedMixGetData(it.feedMixxChickkData)
                                            eggLabelGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(
                                MainApplication.FORTUNE_MAIN_TAG,
                                "Current time less then expired, use saved url"
                            )
                            _chickHealthHomeScreenState.value =
                                FeedMixHomeScreenState.FeedMixSuccess(
                                    fortuneRushLocalStorageManager.feedMixSavedUrl
                                )
                        }
                    } else {
                        _chickHealthHomeScreenState.value =
                            FeedMixHomeScreenState.FeedMixNotInternet
                    }
                }

                2 -> {
                    _chickHealthHomeScreenState.value =
                        FeedMixHomeScreenState.FeedMixError
                }
            }
        }
    }


    private suspend fun feedMixGetData(conversation: MutableMap<String, Any>?) {
        val eggLabelData = fortuneRushGetAllUseCaseInApp.invoke(conversation)
        if (fortuneRushLocalStorageManager.feedMixAppState == 0) {
            if (eggLabelData == null) {
                fortuneRushLocalStorageManager.feedMixAppState = 2
                _chickHealthHomeScreenState.value =
                    FeedMixHomeScreenState.FeedMixError
            } else {
                fortuneRushLocalStorageManager.feedMixAppState = 1
                fortuneRushLocalStorageManager.apply {
                    feedMixExpired = eggLabelData.feedMixExpires
                    feedMixSavedUrl = eggLabelData.feedMixUrl
                }
                _chickHealthHomeScreenState.value =
                    FeedMixHomeScreenState.FeedMixSuccess(eggLabelData.feedMixUrl)
            }
        } else {
            if (eggLabelData == null) {
                _chickHealthHomeScreenState.value =
                    FeedMixHomeScreenState.FeedMixSuccess(fortuneRushLocalStorageManager.feedMixSavedUrl)
            } else {
                fortuneRushLocalStorageManager.apply {
                    feedMixExpired = eggLabelData.feedMixExpires
                    feedMixSavedUrl = eggLabelData.feedMixUrl
                }
                _chickHealthHomeScreenState.value =
                    FeedMixHomeScreenState.FeedMixSuccess(eggLabelData.feedMixUrl)
            }
        }
    }


    sealed class FeedMixHomeScreenState {
        data object FeedMixLoading : FeedMixHomeScreenState()
        data object FeedMixError : FeedMixHomeScreenState()
        data class FeedMixSuccess(val data: String) : FeedMixHomeScreenState()
        data object FeedMixNotInternet : FeedMixHomeScreenState()
    }
}
package com.appslocraapp.slotscrashapp.ui.ie.domain

import com.google.gson.annotations.SerializedName

private const val FEED_MIX_A = "com.appslocraapp.slotscrashapp"
private const val FEED_MIX_B = "fortunerush-29c96"


data class FortuneRushParam (
    @SerializedName("af_id")
    val fortuneRushAfId: String,
    @SerializedName("bundle_id")
    val fortuneRushBundleId: String = FEED_MIX_A,
    @SerializedName("os")
    val feedMixOs: String = "Android",
    @SerializedName("store_id")
    val feedMixStoreId: String = FEED_MIX_A,
    @SerializedName("locale")
    val fortuneRushLocale: String,
    @SerializedName("push_token")
    val fortuneRushPushToken: String,
    @SerializedName("firebase_project_id")
    val feedMixFirebaseProjectId: String = FEED_MIX_B,
)
data class FortuneRushEntity (
    @SerializedName("ok")
    val feedMixOk: String,
    @SerializedName("url")
    val feedMixUrl: String,
    @SerializedName("expires")
    val feedMixExpires: Long,
)
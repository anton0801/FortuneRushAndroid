package com.appslocraapp.slotscrashapp.data.models

data class DailyReward(
    val day: Int,
    val reward: Int,
    var isClaimed: Boolean = false,
    var isActive: Boolean = false
)
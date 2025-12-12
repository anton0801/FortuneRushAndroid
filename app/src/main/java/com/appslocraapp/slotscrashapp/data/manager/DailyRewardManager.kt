package com.appslocraapp.slotscrashapp.data.manager

import android.content.Context
import com.appslocraapp.slotscrashapp.data.models.DailyReward

class DailyRewardManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("daily_rewards", Context.MODE_PRIVATE)
    private val KEY_CLAIMED = "claimed_"
    private val KEY_LAST_CLAIM_TIME = "last_claim_time_day_"
    private val KEY_FIRST_LOGIN = "first_login_time"

    private val rewardsList = listOf(
        DailyReward(day = 1, reward = 1000),
        DailyReward(day = 2, reward = 2000),
        DailyReward(day = 3, reward = 3000),
        DailyReward(day = 4, reward = 4000),
        DailyReward(day = 5, reward = 5000),
        DailyReward(day = 6, reward = 6000),
        DailyReward(day = 7, reward = 7000)
    )

    fun getRewards(): List<DailyReward> {
        val now = System.currentTimeMillis()

        if (!prefs.contains(KEY_FIRST_LOGIN)) {
            prefs.edit().putLong(KEY_FIRST_LOGIN, now).apply()
        }

        var lastClaimedDay = 0
        var lastClaimTime = prefs.getLong(KEY_FIRST_LOGIN, now)

        // Определяем, какие награды уже забрали и какая сейчас активна
        for (i in rewardsList.indices) {
            val day = i + 1
            val claimedKey = "$KEY_CLAIMED$day"
            val isClaimed = prefs.getBoolean(claimedKey, false)

            rewardsList[i].isClaimed = isClaimed

            if (isClaimed) {
                lastClaimedDay = day
                lastClaimTime = prefs.getLong("$KEY_LAST_CLAIM_TIME$day", now)
            }
        }

        val currentDay = lastClaimedDay + 1
        if (currentDay <= 7) {
            val timeToUnlock = lastClaimTime + 24 * 60 * 60 * 1000 // +24 часа
            rewardsList[currentDay - 1].isActive = now >= timeToUnlock
        }

        if (lastClaimedDay == 0) {
            rewardsList[0].isActive = true
        }

        return rewardsList
    }

    fun claimReward(day: Int): Boolean {
        val reward = rewardsList.getOrNull(day - 1) ?: return false
        if (reward.isClaimed || !reward.isActive) return false

        val now = System.currentTimeMillis()

        with(prefs.edit()) {
            putBoolean("$KEY_CLAIMED$day", true)
            putLong("$KEY_LAST_CLAIM_TIME$day", now)
            apply()
        }

        return true
    }

    fun resetAll() {
        prefs.edit().clear().apply()
    }

}
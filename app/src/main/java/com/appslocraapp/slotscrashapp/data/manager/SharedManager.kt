package com.appslocraapp.slotscrashapp.data.manager

import android.content.Context

class SharedManager constructor(
    private val context: Context
) {

    private val sharedPreferences =
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun putString(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String) = sharedPreferences.getString(key, "") ?: ""

    fun putBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun addPoints(points: Int) {
        val currentPoints = sharedPreferences.getInt("points", 0)
        editor?.putInt("points", currentPoints + points)?.apply()
    }

    fun setPoints() {
        editor?.putInt("points", 1_000_000)?.apply()
    }

    fun minusPoints(points: Int) {
        val currentPoints = sharedPreferences.getInt("points", 0)
        editor?.putInt("points", currentPoints - points)?.apply()
    }

    fun getPoints(): Int {
        return sharedPreferences.getInt("points", 0)
    }

    fun setMaxWin(maxWin: Int) {
        editor.putInt("maxWin", maxWin).apply()
    }

    fun getMaxWin(): Int = sharedPreferences.getInt("maxWin", 0)

    fun allSpins() = sharedPreferences.getInt("allSpins", 0)

    fun addSpin() {
        editor.putInt("allSpins", allSpins() + 1).apply()
    }

}
package com.appslocraapp.slotscrashapp.ui.ie.presentation.util

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class FortRushhDataStore : ViewModel(){
    val forrrtttRusshhhhViList: MutableList<ForrrtttRusshhhhVi> = mutableListOf()
    var feedMixIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var feedMixContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var forrrtttRusshhhhView: ForrrtttRusshhhhVi

}
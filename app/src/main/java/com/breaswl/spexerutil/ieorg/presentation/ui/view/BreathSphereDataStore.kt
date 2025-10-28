package com.breaswl.spexerutil.ieorg.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class BreathSphereDataStore : ViewModel(){
    val breathSphereViList: MutableList<BreathSphereVi> = mutableListOf()
    private val _breathSphereIsFirstFinishPage: MutableStateFlow<Boolean> = MutableStateFlow(true)
    var isFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var containerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var breathSphereView: BreathSphereVi
    fun breathSphereSetIsFirstFinishPage() {
        _breathSphereIsFirstFinishPage.value = false
    }
}
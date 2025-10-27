package com.breaswl.spexerutil.ieorg.presentation.ui.view

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class BreathSphereDataStore : ViewModel(){
    val breathSphereViList: MutableList<BreathSphereVi> = mutableListOf()
    private val _breathSphereIsFirstFinishPage: MutableStateFlow<Boolean> = MutableStateFlow(true)

    fun breathSphereSetIsFirstFinishPage() {
        _breathSphereIsFirstFinishPage.value = false
    }
}
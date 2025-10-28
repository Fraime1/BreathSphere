package com.breaswl.spexerutil.ieorg.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.breaswl.spexerutil.ieorg.data.shar.BreathSphereSharedPreference
import com.breaswl.spexerutil.ieorg.data.utils.BreathSphereSystemService
import com.breaswl.spexerutil.ieorg.domain.usecases.BreathSphereGetAllUseCase
import com.breaswl.spexerutil.ieorg.presentation.app.BreathSphereAppsFlyerState
import com.breaswl.spexerutil.ieorg.presentation.app.BreathSphereApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BreathSphereLoadViewModel(
    private val breathSphereGetAllUseCase: BreathSphereGetAllUseCase,
    private val breathSphereSharedPreference: BreathSphereSharedPreference,
    private val breathSphereSystemService: BreathSphereSystemService
) : ViewModel() {

    var breathSphereUrl = ""

    private val _breathSphereHomeScreenState: MutableStateFlow<BreathSphereHomeScreenState> =
        MutableStateFlow(BreathSphereHomeScreenState.BreathSphereLoading)
    val breathSphereHomeScreenState = _breathSphereHomeScreenState.asStateFlow()

    private var breathSphereGetApps = false


    init {
        viewModelScope.launch {
            when (breathSphereSharedPreference.breathSphereAppState) {
                0 -> {
                    if (breathSphereSystemService.breathSphereIsOnline()) {
                        BreathSphereApp.breathSphereConversionFlow.collect {
                            when(it) {
                                BreathSphereAppsFlyerState.BreathSphereDefault -> {}
                                BreathSphereAppsFlyerState.BreathSphereError -> {
                                    breathSphereSharedPreference.breathSphereAppState = 2
                                    _breathSphereHomeScreenState.value =
                                        BreathSphereHomeScreenState.BreathSphereError
                                    breathSphereGetApps = true
                                }
                                is BreathSphereAppsFlyerState.BreathSphereSuccess -> {
                                    if (!breathSphereGetApps) {
                                        breathSphereGetData(it.breathSphereData)
                                        breathSphereGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _breathSphereHomeScreenState.value =
                            BreathSphereHomeScreenState.BreathSphereNotInternet
                    }
                }
                1 -> {
                    if (breathSphereSystemService.breathSphereIsOnline()) {
                        if (BreathSphereApp.BREATH_SPHERE_FB_LI != null) {
                            _breathSphereHomeScreenState.value =
                                BreathSphereHomeScreenState.BreathSphereSuccess(
                                    BreathSphereApp.BREATH_SPHERE_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > breathSphereSharedPreference.breathSphereExpired) {
                            Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "Current time more then expired, repeat request")
                            Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "Сurrent time = ${System.currentTimeMillis() / 1000}")
                            Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "Expired time = ${breathSphereSharedPreference.breathSphereExpired}")


                            BreathSphereApp.breathSphereConversionFlow.collect {
                                when(it) {
                                    BreathSphereAppsFlyerState.BreathSphereDefault -> {}
                                    BreathSphereAppsFlyerState.BreathSphereError -> {
                                        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "SavedUrl from load Apps= ${breathSphereSharedPreference.breathSphereSavedUrl}")
                                        _breathSphereHomeScreenState.value =
                                            BreathSphereHomeScreenState.BreathSphereSuccess(
                                                breathSphereSharedPreference.breathSphereSavedUrl
                                            )
                                        breathSphereGetApps = true
                                    }
                                    is BreathSphereAppsFlyerState.BreathSphereSuccess -> {
                                        if (!breathSphereGetApps) {
                                            breathSphereGetData(it.breathSphereData)
                                            breathSphereGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "Current time less then expired, use saved url")
                            Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "Сurrent time = ${System.currentTimeMillis() / 1000}")
                            Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "Expired time = ${breathSphereSharedPreference.breathSphereExpired}")
                            _breathSphereHomeScreenState.value =
                                BreathSphereHomeScreenState.BreathSphereSuccess(
                                    breathSphereSharedPreference.breathSphereSavedUrl
                                )
                        }
                    } else {
                        _breathSphereHomeScreenState.value =
                            BreathSphereHomeScreenState.BreathSphereNotInternet
                    }
                }
                2 -> {
                    _breathSphereHomeScreenState.value =
                        BreathSphereHomeScreenState.BreathSphereError
                }
            }
        }
    }


    private suspend fun breathSphereGetData(conversation: MutableMap<String, Any>?) {
        val breathSphereData = breathSphereGetAllUseCase.invoke(conversation)
        if (breathSphereSharedPreference.breathSphereAppState == 0) {
            if (breathSphereData == null) {
                breathSphereSharedPreference.breathSphereAppState = 2
                _breathSphereHomeScreenState.value =
                    BreathSphereHomeScreenState.BreathSphereError
            } else {
                breathSphereSharedPreference.breathSphereAppState = 1
                breathSphereSharedPreference.apply {
                    breathSphereExpired = breathSphereData.breathSphereExpires
                    breathSphereSavedUrl = breathSphereData.breathSphereUrl
                }
                _breathSphereHomeScreenState.value =
                    BreathSphereHomeScreenState.BreathSphereSuccess(breathSphereData.breathSphereUrl)
            }
        } else  {
            if (breathSphereData == null) {
                Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "SavedUrl = ${breathSphereSharedPreference.breathSphereSavedUrl}")

                _breathSphereHomeScreenState.value =
                    BreathSphereHomeScreenState.BreathSphereSuccess(breathSphereSharedPreference.breathSphereSavedUrl)
            } else {
                breathSphereSharedPreference.apply {
                    breathSphereExpired = breathSphereData.breathSphereExpires
                    breathSphereSavedUrl = breathSphereData.breathSphereUrl
                }
                _breathSphereHomeScreenState.value =
                    BreathSphereHomeScreenState.BreathSphereSuccess(breathSphereData.breathSphereUrl)
            }
        }
    }


    sealed class BreathSphereHomeScreenState {
        data object BreathSphereLoading : BreathSphereHomeScreenState()
        data object BreathSphereError : BreathSphereHomeScreenState()
        data class BreathSphereSuccess(val data: String) : BreathSphereHomeScreenState()
        data object BreathSphereNotInternet: BreathSphereHomeScreenState()
    }
}
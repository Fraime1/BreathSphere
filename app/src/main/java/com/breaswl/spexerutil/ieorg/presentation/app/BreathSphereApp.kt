package com.breaswl.spexerutil.ieorg.presentation.app

import android.app.Application
import android.view.WindowManager
import com.breaswl.spexerutil.ieorg.data.utils.BreathSphereAppsflyer
import com.breaswl.spexerutil.ieorg.data.utils.BreathSphereSystemService
import com.breaswl.spexerutil.ieorg.presentation.di.breathSphereModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


sealed interface BreathSphereAppsFlyerState {
    data object BreathSphereDefault : BreathSphereAppsFlyerState
    data class BreathSphereSuccess(val breathSphereData: MutableMap<String, Any>?) :
        BreathSphereAppsFlyerState
    data object BreathSphereError : BreathSphereAppsFlyerState
}

class BreathSphereApp : Application() {


    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@BreathSphereApp)
            modules(
                listOf(
                    breathSphereModule
                )
            )
        }
        val breathSphereAppsflyer = BreathSphereAppsflyer(this)
        val breathSphereSystemService = BreathSphereSystemService(this)
        if (breathSphereSystemService.breathSphereIsOnline()) {
            CoroutineScope(Dispatchers.IO).launch {
                breathSphereConversionFlow.value = breathSphereAppsflyer.init()
            }
        }
    }

    companion object {
        var breathSphereInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val breathSphereConversionFlow: MutableStateFlow<BreathSphereAppsFlyerState> = MutableStateFlow(
            BreathSphereAppsFlyerState.BreathSphereDefault
        )
        var BREATH_SPHERE_FB_LI: String? = null
        const val BREATH_SPHERE_MAIN_TAG = "BreathSphereMainTag"
    }
}
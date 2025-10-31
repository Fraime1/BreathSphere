package com.breaswl.spexerutil.ieorg.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.breaswl.spexerutil.ieorg.presentation.di.breathSphereModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface BreathSphereAppsFlyerState {
    data object BreathSphereDefault : BreathSphereAppsFlyerState
    data class BreathSphereSuccess(val breathSphereData: MutableMap<String, Any>?) :
        BreathSphereAppsFlyerState
    data object BreathSphereError : BreathSphereAppsFlyerState
}

private const val BREATH_SPHERE_APP_DEV = "pijPckGxdvTHp6mxFiBiw9"
private const val BREATH_SPHERE_LIN = "com.breaswl.spexerutil"
interface BreathSphereAppsApi {
    @Headers("Content-Type: application/json")
    @GET(BREATH_SPHERE_LIN)
    fun breathSphereGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}
class BreathSphereApp : Application() {

    private var breathSphereIsResumed = false

    override fun onCreate() {
        super.onCreate()
        val appsflyer = AppsFlyerLib.getInstance()
        breathSphereSetDebufLogger(appsflyer)
        breathSphereMinTimeBetween(appsflyer)


        appsflyer.init(
            BREATH_SPHERE_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    Log.d(BREATH_SPHERE_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = breathSphereGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.breathSphereGetClient(
                                    devkey = BREATH_SPHERE_APP_DEV,
                                    deviceId = breathSphereGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(BREATH_SPHERE_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic") {
                                    safeResume(BreathSphereAppsFlyerState.BreathSphereError)
                                } else {
                                    safeResume(
                                        BreathSphereAppsFlyerState.BreathSphereSuccess(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(BREATH_SPHERE_MAIN_TAG, "Error: ${d.message}")
                                safeResume(BreathSphereAppsFlyerState.BreathSphereError)
                            }
                        }
                    } else {
                        safeResume(BreathSphereAppsFlyerState.BreathSphereSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    Log.d(BREATH_SPHERE_MAIN_TAG, "onConversionDataFail: $p0")
                    safeResume(BreathSphereAppsFlyerState.BreathSphereError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(BREATH_SPHERE_MAIN_TAG, "onAppOpenAttribution")
//                        safeResume(BreathSphereAppsFlyerState.BreathSphereError)
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(BREATH_SPHERE_MAIN_TAG, "onAttributionFailure: $p0")
//                        safeResume(BreathSphereAppsFlyerState.BreathSphereError)
                }
            },
            this
        )

        appsflyer.start(this, BREATH_SPHERE_APP_DEV, object :
                AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(BREATH_SPHERE_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(BREATH_SPHERE_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
                safeResume(BreathSphereAppsFlyerState.BreathSphereError)
            }
        })
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@BreathSphereApp)
            modules(
                listOf(
                    breathSphereModule
                )
            )
        }
    }

    private fun safeResume(state: BreathSphereAppsFlyerState) {
        if (!breathSphereIsResumed) {
            breathSphereIsResumed = true
            breathSphereConversionFlow.value = state
        }
    }

    private fun breathSphereGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(BREATH_SPHERE_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun breathSphereSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun breathSphereMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun breathSphereGetApi(url: String, client: OkHttpClient?) : BreathSphereAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
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
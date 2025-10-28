package com.breaswl.spexerutil.ieorg.data.utils

import android.content.Context
import android.os.Looper
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.breaswl.spexerutil.ieorg.presentation.app.BreathSphereApp
import com.breaswl.spexerutil.ieorg.presentation.app.BreathSphereAppsFlyerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


//private const val BREATH_SPHERE_APP_DEV = "pijPckGxdvTHp6mxFiBiw9"
//private const val BREATH_SPHERE_LIN = "com.breaswl.spexerutil"
//class BreathSphereAppsflyer(private val context: Context) {
//
//
//    suspend fun init(): BreathSphereAppsFlyerState = withContext(Dispatchers.Main) {
//        suspendCoroutine { cont ->
//            val appsflyer = AppsFlyerLib.getInstance()
//            breathSphereSetDebufLogger(appsflyer)
//            breathSphereMinTimeBetween(appsflyer)
//
//            var isResumed = false
//            fun safeResume(state: BreathSphereAppsFlyerState) {
//                if (!isResumed) {
//                    isResumed = true
//                    cont.resume(state)
//                }
//            }
//
//            appsflyer.init(
//                BREATH_SPHERE_APP_DEV,
//                object : AppsFlyerConversionListener {
//                    override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
//                        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "onConversionDataSuccess: $p0")
//
//                        val afStatus = p0?.get("af_status")?.toString() ?: "null"
//                        if (afStatus == "Organic") {
//                            CoroutineScope(Dispatchers.IO).launch {
//                                try {
//                                    delay(5000)
//                                    val api = breathSphereGetApi(
//                                        "https://gcdsdk.appsflyer.com/install_data/v4.0/",
//                                        null
//                                    )
//                                    val response = api.breathSphereGetClient(
//                                        devkey = BREATH_SPHERE_APP_DEV,
//                                        deviceId = breathSphereGetAppsflyerId()
//                                    ).awaitResponse()
//
//                                    val resp = response.body()
//                                    Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "After 5s: $resp")
//                                    if (resp?.get("af_status") == "Organic") {
//                                        safeResume(BreathSphereAppsFlyerState.BreathSphereError)
//                                    } else {
//                                        safeResume(
//                                            BreathSphereAppsFlyerState.BreathSphereSuccess(resp)
//                                        )
//                                    }
//                                } catch (d: Exception) {
//                                    Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "Error: ${d.message}")
//                                    safeResume(BreathSphereAppsFlyerState.BreathSphereError)
//                                }
//                            }
//                        } else {
//                            safeResume(BreathSphereAppsFlyerState.BreathSphereSuccess(p0))
//                        }
//                    }
//
//                    override fun onConversionDataFail(p0: String?) {
//                        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "onConversionDataFail: $p0")
//                        safeResume(BreathSphereAppsFlyerState.BreathSphereError)
//                    }
//
//                    override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
//                        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "onAppOpenAttribution")
////                        safeResume(BreathSphereAppsFlyerState.BreathSphereError)
//                    }
//
//                    override fun onAttributionFailure(p0: String?) {
//                        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "onAttributionFailure: $p0")
////                        safeResume(BreathSphereAppsFlyerState.BreathSphereError)
//                    }
//                },
//                context.applicationContext
//            )
//
//            appsflyer.start(context.applicationContext, BREATH_SPHERE_APP_DEV, object : AppsFlyerRequestListener {
//                override fun onSuccess() {
//                    Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "AppsFlyer started")
//                }
//
//                override fun onError(p0: Int, p1: String) {
//                    Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
//                    safeResume(BreathSphereAppsFlyerState.BreathSphereError)
//                }
//            })
//        }
//    }
//
//    private fun breathSphereGetAppsflyerId(): String {
//        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(context) ?: ""
//        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
//        return appsflyrid
//    }
//
//    private fun breathSphereSetDebufLogger(appsflyer: AppsFlyerLib) {
//        appsflyer.setDebugLog(true)
//    }
//
//    private fun breathSphereMinTimeBetween(appsflyer: AppsFlyerLib) {
//        appsflyer.setMinTimeBetweenSessions(0)
//    }
//
//    private fun breathSphereGetApi(url: String, client: OkHttpClient?) : BreathSphereAppsApi {
//        val retrofit = Retrofit.Builder()
//            .baseUrl(url)
//            .client(client ?: OkHttpClient.Builder().build())
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        return retrofit.create()
//    }
//
//}
//
//
//interface BreathSphereAppsApi {
//    @Headers("Content-Type: application/json")
//    @GET(BREATH_SPHERE_LIN)
//    fun breathSphereGetClient(
//        @Query("devkey") devkey: String,
//        @Query("device_id") deviceId: String,
//    ): Call<MutableMap<String, Any>?>
//}
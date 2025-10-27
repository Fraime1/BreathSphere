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
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


private const val BREATH_SPHERE_APP_DEV = "pijPckGxdvTHp6mxFiBiw9"
private const val BREATH_SPHERE_LIN = "com.breaswl.spexerutil"
class BreathSphereAppsflyer(private val context: Context) {


    fun init(
        breathSphereCallback: (BreathSphereAppsFlyerState) -> Unit
    ) {
        val appsflyer = AppsFlyerLib.getInstance()
        breathSphereSetDebufLogger(appsflyer)
        breathSphereMinTimeBetween(appsflyer)
        appsflyer.init(
            BREATH_SPHERE_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    Looper.prepare()
                    Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "AppsFlyer: onConversionDataSuccess")
                    Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "AppsFlyer: $p0")
                    Log.d(
                        BreathSphereApp.BREATH_SPHERE_MAIN_TAG,
                        "AppsFlyer: af_status: ${p0?.get("af_status")}"
                    )
//                    breathSphereCallback(BubblePasswordAppsFlyerState.BubblePasswordSuccess(p0))
                    if (p0?.get("af_status") == "Organic") {
                        val corouteScope = CoroutineScope(Dispatchers.IO)
                        corouteScope.launch {
                            try {
                                delay(5000)
                                val api = breathSphereGetApi("https://gcdsdk.appsflyer.com/install_data/v4.0/", null)
                                val request = api.breathSphereGetClient(
                                    devkey = BREATH_SPHERE_APP_DEV,
                                    deviceId = breathSphereGetAppsflyerId()
                                )
                                val response = request.awaitResponse()
                                Log.d(
                                    BreathSphereApp.BREATH_SPHERE_MAIN_TAG,
                                    "AppsFlyer: Conversion after 5 seconds: ${response.body()}"
                                )
                                if (response.body()?.get("af_status") == "Organic") {
                                    breathSphereCallback(BreathSphereAppsFlyerState.BreathSphereError)
                                } else {
                                    breathSphereCallback(BreathSphereAppsFlyerState.BreathSphereSuccess(response.body()))
                                }
                            } catch (e: Exception) {
                                Log.d(
                                    BreathSphereApp.BREATH_SPHERE_MAIN_TAG,
                                    "AppsFlyer: ${e.message}"
                                )
                                breathSphereCallback(BreathSphereAppsFlyerState.BreathSphereError)
                            }
                        }
                    } else {
                        breathSphereCallback(BreathSphereAppsFlyerState.BreathSphereSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "AppsFlyer: onConversionDataFail: $p0")
                    breathSphereCallback(BreathSphereAppsFlyerState.BreathSphereError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "AppsFlyer: onAppOpenAttribution")
                    breathSphereCallback(BreathSphereAppsFlyerState.BreathSphereError)
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "AppsFlyer: onAttributionFailure: $p0")
                    breathSphereCallback(BreathSphereAppsFlyerState.BreathSphereError)
                }
            },
            context.applicationContext
        )
        appsflyer.start(context, BREATH_SPHERE_APP_DEV, object : AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "AppsFlyer: Start is Success")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "AppsFlyer: Start is Error")
                Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "AppsFlyer: Error code: $p0, error message: $p1")
                breathSphereCallback(BreathSphereAppsFlyerState.BreathSphereError)
            }

        })
    }

    private fun breathSphereGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(context) ?: ""
        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
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

}


interface BreathSphereAppsApi {
    @Headers("Content-Type: application/json")
    @GET(BREATH_SPHERE_LIN)
    fun breathSphereGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}
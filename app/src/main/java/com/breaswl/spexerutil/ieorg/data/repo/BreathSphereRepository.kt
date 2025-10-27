package com.breaswl.spexerutil.ieorg.data.repo

import android.util.Log
import com.breaswl.spexerutil.ieorg.domain.model.BreathSphereEntity
import com.breaswl.spexerutil.ieorg.domain.model.BreathSphereParam
import com.breaswl.spexerutil.ieorg.presentation.app.BreathSphereApp.Companion.BREATH_SPHERE_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface BreathSphereApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun getClient(
        @Body jsonString: JsonObject,
    ): Call<BreathSphereEntity>
}


private const val BREATH_SPHERE_MAIN = "https://breatthspheere.com/"
class BreathSphereRepository {

    suspend fun breathSphereGetClient(
        breathSphereParam: BreathSphereParam,
        breathSphereConversion: MutableMap<String, Any>?
    ): BreathSphereEntity? {
        val gson = Gson()
        val api = breathSphereGetApi(BREATH_SPHERE_MAIN, null)

        val breathSphereJsonObject = gson.toJsonTree(breathSphereParam).asJsonObject
        breathSphereConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            breathSphereJsonObject.add(key, element)
        }
        return try {
            val breathSphereRequest: Call<BreathSphereEntity> = api.getClient(
                jsonString = breathSphereJsonObject,
            )
            val breathSphereResult = breathSphereRequest.awaitResponse()
            Log.d(BREATH_SPHERE_MAIN_TAG, "Retrofit: Result code: ${breathSphereResult.code()}")
            if (breathSphereResult.code() == 200) {
                Log.d(BREATH_SPHERE_MAIN_TAG, "Retrofit: Get request success")
                Log.d(BREATH_SPHERE_MAIN_TAG, "Retrofit: Code = ${breathSphereResult.code()}")
                Log.d(BREATH_SPHERE_MAIN_TAG, "Retrofit: ${breathSphereResult.body()}")
                breathSphereResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(BREATH_SPHERE_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(BREATH_SPHERE_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun breathSphereGetApi(url: String, client: OkHttpClient?) : BreathSphereApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}

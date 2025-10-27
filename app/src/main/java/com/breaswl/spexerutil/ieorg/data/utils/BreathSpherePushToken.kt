package com.breaswl.spexerutil.ieorg.data.utils

import android.util.Log
import com.breaswl.spexerutil.ieorg.presentation.app.BreathSphereApp
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BreathSpherePushToken {

    suspend fun breathSphereGetToken(): String = suspendCoroutine { continuation ->
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (!it.isSuccessful) {
                    continuation.resume(it.result)
                    Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "Token error: ${it.exception}")
                } else {
                    continuation.resume(it.result)
                }
            }
        } catch (e: Exception) {
            Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "FirebaseMessagingPushToken = null")
            continuation.resume("")
        }
    }


}
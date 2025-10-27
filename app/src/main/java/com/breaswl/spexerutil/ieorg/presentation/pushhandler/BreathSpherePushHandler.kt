package com.breaswl.spexerutil.ieorg.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.breaswl.spexerutil.ieorg.presentation.app.BreathSphereApp

class BreathSpherePushHandler() {
    fun breathSphereHandlePush(extras: Bundle?) {
        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = breathSphereBundleToMap(extras)
            Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    BreathSphereApp.BREATH_SPHERE_FB_LI = map["url"]
                    Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "Push data no!")
        }
    }

    private fun breathSphereBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}
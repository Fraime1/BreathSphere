package com.breaswl.spexerutil.ieorg.data.shar

import android.content.Context
import androidx.core.content.edit

class BreathSphereSharedPreference(context: Context) {
    private val breathSpherePrefs = context.getSharedPreferences("breathSphereSharedPrefsAb", Context.MODE_PRIVATE)

    var breathSphereSavedUrl: String
        get() = breathSpherePrefs.getString(BREATH_SPHERE_SAVED_URL, "") ?: ""
        set(value) = breathSpherePrefs.edit { putString(BREATH_SPHERE_SAVED_URL, value) }

    var breathSphereExpired : Long
        get() = breathSpherePrefs.getLong(BREATH_SPHERE_EXPIRED, 0L)
        set(value) = breathSpherePrefs.edit { putLong(BREATH_SPHERE_EXPIRED, value) }

    var breathSphereAppState: Int
        get() = breathSpherePrefs.getInt(BREATH_SPHERE_APPLICATION_STATE, 0)
        set(value) = breathSpherePrefs.edit { putInt(BREATH_SPHERE_APPLICATION_STATE, value) }

    var breathSphereNotificationRequest: Long
        get() = breathSpherePrefs.getLong(BREATH_SPHERE_NOTIFICAITON_REQUEST, 0L)
        set(value) = breathSpherePrefs.edit { putLong(BREATH_SPHERE_NOTIFICAITON_REQUEST, value) }

    var breathSphereNotificationRequestedBefore: Boolean
        get() = breathSpherePrefs.getBoolean(BREATH_SPHERE_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = breathSpherePrefs.edit { putBoolean(
            BREATH_SPHERE_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val BREATH_SPHERE_SAVED_URL = "breathSphereSavedUrl"
        private const val BREATH_SPHERE_EXPIRED = "breathSphereExpired"
        private const val BREATH_SPHERE_APPLICATION_STATE = "breathSphereApplicationState"
        private const val BREATH_SPHERE_NOTIFICAITON_REQUEST = "breathSphereNotificationRequest"
        private const val BREATH_SPHERE_NOTIFICATION_REQUEST_BEFORE = "breathSphereNotificationRequestedBefore"
    }
}
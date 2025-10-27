package com.breaswl.spexerutil.ieorg.domain.model

import com.google.gson.annotations.SerializedName


private const val BREATH_SPHERE_A = "com.breaswl.spexerutil"
private const val BREATH_SPHERE_B = "breathsphere-f6e5a"
data class BreathSphereParam (
    @SerializedName("af_id")
    val breathSphereAfId: String,
    @SerializedName("bundle_id")
    val breathSphereBundleId: String = BREATH_SPHERE_A,
    @SerializedName("os")
    val breathSphereOs: String = "Android",
    @SerializedName("store_id")
    val breathSphereStoreId: String = BREATH_SPHERE_A,
    @SerializedName("locale")
    val breathSphereLocale: String,
    @SerializedName("push_token")
    val breathSpherePushToken: String,
    @SerializedName("firebase_project_id")
    val breathSphereFirebaseProjectId: String = BREATH_SPHERE_B,

    )
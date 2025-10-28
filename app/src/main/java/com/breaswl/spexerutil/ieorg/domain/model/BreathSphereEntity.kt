package com.breaswl.spexerutil.ieorg.domain.model

import com.google.gson.annotations.SerializedName


data class BreathSphereEntity (
    @SerializedName("ok")
    val breathSphereOk: String,
    @SerializedName("url")
    val breathSphereUrl: String,
    @SerializedName("expires")
    val breathSphereExpires: Long,
)
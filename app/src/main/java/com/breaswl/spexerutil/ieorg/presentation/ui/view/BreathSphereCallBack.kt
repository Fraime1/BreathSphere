package com.breaswl.spexerutil.ieorg.presentation.ui.view


import android.content.Context
import android.net.Uri
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest

interface BreathSphereCallBack {
    fun breathSphereHandleCreateWebWindowRequest(breathSphereVi: BreathSphereVi)

    fun breathSphereOnPermissionRequest(breathSphereRequest: PermissionRequest?)

    fun breathSphereOnFirstPageFinished()
}
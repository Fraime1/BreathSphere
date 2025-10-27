package com.breaswl.spexerutil.ieorg.presentation.ui.view


import android.net.Uri
import android.webkit.PermissionRequest
import android.webkit.ValueCallback

interface BreathSphereCallBack {
    fun breathSphereHandleCreateWebWindowRequest(breathSphereVi: BreathSphereVi)

    fun breathSphereOnPermissionRequest(breathSphereRequest: PermissionRequest?)

    fun breathSphereOnShowFileChooser(breathSphereFilePathCallback: ValueCallback<Array<Uri>>?)

    fun breathSphereOnFirstPageFinished()
}
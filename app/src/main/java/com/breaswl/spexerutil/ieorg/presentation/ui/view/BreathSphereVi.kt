package com.breaswl.spexerutil.ieorg.presentation.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import com.breaswl.spexerutil.ieorg.presentation.app.BreathSphereApp

class BreathSphereVi(
    private val breathSphereContext: Context,
    private val breathSphereCallback: BreathSphereCallBack,
    private val breathSphereWindow: Window
) : WebView(breathSphereContext) {
    private var breathSphereFileChooserHandler: ((ValueCallback<Array<Uri>>?) -> Unit)? = null

    fun breathSphereSetFileChooserHandler(handler: (ValueCallback<Array<Uri>>?) -> Unit) {
        this.breathSphereFileChooserHandler = handler
        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "File chooser handler set")
    }
    init {
        val webSettings = settings
        webSettings.apply {
            setSupportMultipleWindows(true)
            allowFileAccess = true
            allowContentAccess = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            userAgentString = WebSettings.getDefaultUserAgent(breathSphereContext).replace("; wv)", "").replace("Version/4.0 ", "")
            @SuppressLint("SetJavaScriptEnabled")
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        isNestedScrollingEnabled = true



        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        super.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                val link = request?.url?.toString() ?: ""
                Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "Url form w = $link")


                return if (request?.isRedirect == true) {
                    view?.loadUrl(request?.url.toString())
                    true
                }
                else if (URLUtil.isNetworkUrl(link)) {
                    false
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    try {
                        breathSphereContext.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(breathSphereContext, "This application not found", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                CookieManager.getInstance().flush()
                breathSphereCallback.breathSphereOnFirstPageFinished()
                Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "onPageFinished = $url")
                if (url?.contains("ninecasino") == true) {
                    BreathSphereApp.breathSphereInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                    Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "onPageFinished : ${BreathSphereApp.breathSphereInputMode}")
                    breathSphereWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                } else {
                    BreathSphereApp.breathSphereInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "onPageFinished : ${BreathSphereApp.breathSphereInputMode}")
                    breathSphereWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            }


        })

        super.setWebChromeClient(object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                breathSphereCallback.breathSphereOnPermissionRequest(request)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: WebChromeClient.FileChooserParams?,
            ): Boolean {
                breathSphereFileChooserHandler?.invoke(filePathCallback)
                return true
            }
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                breathSphereHandleCreateWebWindowRequest(resultMsg)
                return true
            }
        })
    }



    fun breathSphereFLoad(link: String) {
        super.loadUrl(link)
    }

    private fun breathSphereHandleCreateWebWindowRequest(resultMsg: Message?) {
        if (resultMsg == null) return
        if (resultMsg.obj != null && resultMsg.obj is WebView.WebViewTransport) {
            val transport = resultMsg.obj as WebView.WebViewTransport
            val windowWebView = BreathSphereVi(breathSphereContext, breathSphereCallback, breathSphereWindow)
            transport.webView = windowWebView
            resultMsg.sendToTarget()
            breathSphereCallback.breathSphereHandleCreateWebWindowRequest(windowWebView)
        }
    }


}
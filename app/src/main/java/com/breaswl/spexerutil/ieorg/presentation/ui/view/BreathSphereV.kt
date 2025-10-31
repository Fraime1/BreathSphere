package com.breaswl.spexerutil.ieorg.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.breaswl.spexerutil.ieorg.presentation.app.BreathSphereApp
import com.breaswl.spexerutil.ieorg.presentation.ui.load.BreathSphereLoadFragment
import org.koin.android.ext.android.inject

class BreathSphereV : Fragment(){


    private lateinit var breathSpherePhoto: Uri
    private var breathSphereFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val breathSphereTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        breathSphereFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        breathSphereFilePathFromChrome = null
    }

    private val breathSphereTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            breathSphereFilePathFromChrome?.onReceiveValue(arrayOf(breathSpherePhoto))
            breathSphereFilePathFromChrome = null
        } else {
            breathSphereFilePathFromChrome?.onReceiveValue(null)
            breathSphereFilePathFromChrome = null
        }
    }

    private val breathSphereDataStore by activityViewModels<BreathSphereDataStore>()


    private val breathSphereViFun by inject<BreathSphereViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (breathSphereDataStore.breathSphereView.canGoBack()) {
                        breathSphereDataStore.breathSphereView.goBack()
                        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "WebView can go back")
                    } else if (breathSphereDataStore.breathSphereViList.size > 1) {
                        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "WebView can`t go back")
                        breathSphereDataStore.breathSphereViList.removeAt(breathSphereDataStore.breathSphereViList.lastIndex)
                        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "WebView list size ${breathSphereDataStore.breathSphereViList.size}")
                        breathSphereDataStore.breathSphereView.destroy()
                        val previousWebView = breathSphereDataStore.breathSphereViList.last()
                        breathSphereAttachWebViewToContainer(previousWebView)
                        breathSphereDataStore.breathSphereView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (breathSphereDataStore.breathSphereIsFirstCreate) {
            breathSphereDataStore.breathSphereIsFirstCreate = false
            breathSphereDataStore.breathSphereContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return breathSphereDataStore.breathSphereContainerView
        } else {
            return breathSphereDataStore.breathSphereContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "onViewCreated")
        if (breathSphereDataStore.breathSphereViList.isEmpty()) {
            breathSphereDataStore.breathSphereView = BreathSphereVi(requireContext(), object :
                BreathSphereCallBack {
                override fun breathSphereHandleCreateWebWindowRequest(breathSphereVi: BreathSphereVi) {
                    breathSphereDataStore.breathSphereViList.add(breathSphereVi)
                    Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "WebView list size = ${breathSphereDataStore.breathSphereViList.size}")
                    Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "CreateWebWindowRequest")
                    breathSphereDataStore.breathSphereView = breathSphereVi
                    breathSphereVi.breathSphereSetFileChooserHandler { callback ->
                        breathSphereHandleFileChooser(callback)
                    }
                    breathSphereAttachWebViewToContainer(breathSphereVi)
                }

                override fun breathSphereOnPermissionRequest(breathSphereRequest: PermissionRequest?) {
                    breathSphereRequest?.grant(breathSphereRequest.resources)
                }

                override fun breathSphereOnFirstPageFinished() {
                    breathSphereDataStore.breathSphereSetIsFirstFinishPage()
                }

            }, breathSphereWindow = requireActivity().window).apply {
                breathSphereSetFileChooserHandler { callback ->
                    breathSphereHandleFileChooser(callback)
                }
            }
            breathSphereDataStore.breathSphereView.breathSphereFLoad(arguments?.getString(BreathSphereLoadFragment.BREATH_SPHERE_D) ?: "")
//            ejvview.fLoad("www.google.com")
            breathSphereDataStore.breathSphereViList.add(breathSphereDataStore.breathSphereView)
            breathSphereAttachWebViewToContainer(breathSphereDataStore.breathSphereView)
        } else {
            breathSphereDataStore.breathSphereViList.forEach { webView ->
                webView.breathSphereSetFileChooserHandler { callback ->
                    breathSphereHandleFileChooser(callback)
                }
            }
            breathSphereDataStore.breathSphereView = breathSphereDataStore.breathSphereViList.last()

            breathSphereAttachWebViewToContainer(breathSphereDataStore.breathSphereView)
        }
        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "WebView list size = ${breathSphereDataStore.breathSphereViList.size}")
    }

    private fun breathSphereHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        breathSphereFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "Launching file picker")
                    breathSphereTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "Launching camera")
                    breathSpherePhoto = breathSphereViFun.breathSphereSavePhoto()
                    breathSphereTakePhoto.launch(breathSpherePhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                breathSphereFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun breathSphereAttachWebViewToContainer(w: BreathSphereVi) {
        breathSphereDataStore.breathSphereContainerView.post {
            // Убираем предыдущую WebView, если есть
            (w.parent as? ViewGroup)?.removeView(w)
            breathSphereDataStore.breathSphereContainerView.removeAllViews()
            breathSphereDataStore.breathSphereContainerView.addView(w)
        }
    }




}
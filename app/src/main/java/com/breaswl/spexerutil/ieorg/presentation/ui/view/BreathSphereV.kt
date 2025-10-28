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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.breaswl.spexerutil.BreatheSphereActivity
import com.breaswl.spexerutil.R
import com.breaswl.spexerutil.ieorg.presentation.app.BreathSphereApp
import com.breaswl.spexerutil.ieorg.presentation.ui.load.BreathSphereLoadFragment
import org.koin.android.ext.android.inject

class BreathSphereV : Fragment(){

    private val breathSphereDataStore by activityViewModels<BreathSphereDataStore>()
    lateinit var breathSphereRequestFromChrome: PermissionRequest


    private val breathSphereViFun by inject<BreathSphereViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (breathSphereDataStore.breathSphereView.canGoBack()) {
                        breathSphereDataStore.breathSphereView.goBack()
                        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "WebView can go back")
                    } else if (breathSphereDataStore.breathSphereViList.size > 1) {
                        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "WebView can`t go back")
//                        this.isEnabled = false
                        breathSphereDataStore.breathSphereViList.removeAt(breathSphereDataStore.breathSphereViList.lastIndex)
                        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "WebView list size ${breathSphereDataStore.breathSphereViList.size}")
                        breathSphereDataStore.breathSphereView.destroy()
                        val previousWebView = breathSphereDataStore.breathSphereViList.last()
                        attachWebViewToContainer(previousWebView)
                        breathSphereDataStore.breathSphereView = previousWebView
//                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (breathSphereDataStore.isFirstCreate) {
            breathSphereDataStore.isFirstCreate = false
            breathSphereDataStore.containerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return breathSphereDataStore.containerView
        } else {
            return breathSphereDataStore.containerView
        }
//        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "onCreateView")
//        containerView = FrameLayout(requireContext()).apply {
//            layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//            )
//            id = View.generateViewId()
//        }
//        return containerView
//        return breathSphereDataStore.breathSphereView
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
                    attachWebViewToContainer(breathSphereVi)
                }

                override fun breathSphereOnPermissionRequest(breathSphereRequest: PermissionRequest?) {
                    if (breathSphereRequest != null) {
                        breathSphereRequestFromChrome = breathSphereRequest
                    }
                    breathSphereRequestFromChrome.grant(breathSphereRequestFromChrome.resources)
                }

                override fun breathSphereOnShowFileChooser(breathSphereFilePathCallback: ValueCallback<Array<Uri>>?) {
                    (requireActivity() as BreatheSphereActivity).breathSphereFilePathFromChrome = breathSphereFilePathCallback
                    val listItems: Array<out String> =
                        arrayOf("Select from file", "To make a photo")
                    val listener = DialogInterface.OnClickListener { _, which ->
                        when (which) {
                            0 -> {
                                (requireActivity() as BreatheSphereActivity).breathSphereTakeFile.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            1 -> {
                                (requireActivity() as BreatheSphereActivity).breathSpherePhoto = breathSphereViFun.breathSphereSavePhoto()
                                (requireActivity() as BreatheSphereActivity).breathSphereTakePhoto.launch((requireActivity() as BreatheSphereActivity).breathSpherePhoto)
                            }
                        }
                    }
                    AlertDialog.Builder(requireActivity())
                        .setTitle("Choose a method")
                        .setItems(listItems, listener)
                        .setCancelable(true)
                        .setOnCancelListener {
                            breathSphereFilePathCallback?.onReceiveValue(arrayOf(Uri.EMPTY))
                        }
                        .create()
                        .show()
                }

                override fun breathSphereOnFirstPageFinished() {
                    breathSphereDataStore.breathSphereSetIsFirstFinishPage()
                }

            }, breathSphereWindow = requireActivity().window)
            breathSphereDataStore.breathSphereView.breathSphereFLoad(arguments?.getString(BreathSphereLoadFragment.BREATH_SPHERE_D) ?: "")
//            ejvview.fLoad("www.google.com")
            breathSphereDataStore.breathSphereViList.add(breathSphereDataStore.breathSphereView)
            attachWebViewToContainer(breathSphereDataStore.breathSphereView)
        } else {
            breathSphereDataStore.breathSphereView = breathSphereDataStore.breathSphereViList.last()
            attachWebViewToContainer(breathSphereDataStore.breathSphereView)
        }
        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "WebView list size = ${breathSphereDataStore.breathSphereViList.size}")
    }

    private fun attachWebViewToContainer(w: BreathSphereVi) {
        breathSphereDataStore.containerView.post {
            // Убираем предыдущую WebView, если есть
            (w.parent as? ViewGroup)?.removeView(w)
            breathSphereDataStore.containerView.removeAllViews()
            breathSphereDataStore.containerView.addView(w)
        }
    }




}
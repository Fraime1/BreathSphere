package com.breaswl.spexerutil.ieorg.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.breaswl.spexerutil.BreatheSphereActivity
import com.breaswl.spexerutil.R
import com.breaswl.spexerutil.ieorg.presentation.ui.load.BreathSphereLoadFragment
import org.koin.android.ext.android.inject

class BreathSphereV : Fragment(){

    private val breathSphereDataStore by activityViewModels<BreathSphereDataStore>()
    private lateinit var breathSphereView: BreathSphereVi
    lateinit var breathSphereRequestFromChrome: PermissionRequest


    private val breathSphereViFun by inject<BreathSphereViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (breathSphereView.canGoBack()) {
                        breathSphereView.goBack()
                    } else if (breathSphereDataStore.breathSphereViList.size > 1) {
                        this.isEnabled = false
                        breathSphereDataStore.breathSphereViList.removeAt(breathSphereDataStore.breathSphereViList.lastIndex)
                        breathSphereView.destroy()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (breathSphereDataStore.breathSphereViList.isEmpty()) {
            breathSphereView = BreathSphereVi(requireContext(), object :
                BreathSphereCallBack {
                override fun breathSphereHandleCreateWebWindowRequest(breathSphereVi: BreathSphereVi) {
                    breathSphereDataStore.breathSphereViList.add(breathSphereVi)
                    findNavController().navigate(R.id.action_breathSphereV_self)
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
            breathSphereView.breathSphereFLoad(arguments?.getString(BreathSphereLoadFragment.BREATH_SPHERE_D) ?: "")
//            ejvview.fLoad("www.google.com")
            breathSphereDataStore.breathSphereViList.add(breathSphereView)
        } else {
            breathSphereView = breathSphereDataStore.breathSphereViList.last()
        }
        return breathSphereView
    }




}
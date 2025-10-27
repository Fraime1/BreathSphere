package com.breaswl.spexerutil.ieorg.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.breaswl.spexerutil.MainActivity
import com.breaswl.spexerutil.R
import com.breaswl.spexerutil.databinding.FragmentLoadBreatheSphereBinding
import com.breaswl.spexerutil.ieorg.data.shar.BreathSphereSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class BreathSphereLoadFragment : Fragment(R.layout.fragment_load_breathe_sphere) {
    private lateinit var breathSphereLoadBinding: FragmentLoadBreatheSphereBinding

    private val breathSphereLoadViewModel by viewModel<BreathSphereLoadViewModel>()

    private val breathSphereSharedPreference by inject<BreathSphereSharedPreference>()
    

    private val breathSphereRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            breathSphereNavigateToSuccess(breathSphereLoadViewModel.breathSphereUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                breathSphereSharedPreference.breathSphereNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                breathSphereNavigateToSuccess(breathSphereLoadViewModel.breathSphereUrl)
            } else {
                breathSphereNavigateToSuccess(breathSphereLoadViewModel.breathSphereUrl)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        breathSphereLoadBinding = FragmentLoadBreatheSphereBinding.bind(view)

        breathSphereLoadBinding.breathSphereGrandButton.setOnClickListener {
            val breathSpherePermission = Manifest.permission.POST_NOTIFICATIONS
            breathSphereRequestNotificationPermission.launch(breathSpherePermission)
            breathSphereSharedPreference.breathSphereNotificationRequestedBefore = true
        }

        breathSphereLoadBinding.breathSphereSkipButton.setOnClickListener {
            breathSphereSharedPreference.breathSphereNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            breathSphereNavigateToSuccess(breathSphereLoadViewModel.breathSphereUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                breathSphereLoadViewModel.breathSphereHomeScreenState.collect {
                    when (it) {
                        is BreathSphereLoadViewModel.BreathSphereHomeScreenState.BreathSphereLoading -> {

                        }

                        is BreathSphereLoadViewModel.BreathSphereHomeScreenState.BreathSphereError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is BreathSphereLoadViewModel.BreathSphereHomeScreenState.BreathSphereSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val breathSpherePermission = Manifest.permission.POST_NOTIFICATIONS
                                val breathSpherePermissionRequestedBefore = breathSphereSharedPreference.breathSphereNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(requireContext(), breathSpherePermission) == PackageManager.PERMISSION_GRANTED) {
                                    breathSphereNavigateToSuccess(it.data)
                                } else if (!breathSpherePermissionRequestedBefore && (System.currentTimeMillis() / 1000 > breathSphereSharedPreference.breathSphereNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    breathSphereLoadBinding.breathSphereNotiGroup.visibility = View.VISIBLE
                                    breathSphereLoadBinding.breathSphereLoadingGroup.visibility = View.GONE
                                    breathSphereLoadViewModel.breathSphereUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(breathSpherePermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > breathSphereSharedPreference.breathSphereNotificationRequest) {
                                        breathSphereLoadBinding.breathSphereNotiGroup.visibility = View.VISIBLE
                                        breathSphereLoadBinding.breathSphereLoadingGroup.visibility = View.GONE
                                        breathSphereLoadViewModel.breathSphereUrl = it.data
                                    } else {
                                        breathSphereNavigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    breathSphereNavigateToSuccess(it.data)
                                }
                            } else {
                                breathSphereNavigateToSuccess(it.data)
                            }
                        }

                        BreathSphereLoadViewModel.BreathSphereHomeScreenState.BreathSphereNotInternet -> {
                            breathSphereLoadBinding.breathSphereLoadConnectionStateText.visibility = View.VISIBLE
                            breathSphereLoadBinding.breathSphereLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun breathSphereNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_breathSphereLoadFragment_to_breathSphereV,
            bundleOf(BREATH_SPHERE_D to data)
        )
    }

    companion object {
        const val BREATH_SPHERE_D = "breathSphereData"
    }
}
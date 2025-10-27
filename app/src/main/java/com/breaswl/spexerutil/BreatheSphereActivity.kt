package com.breaswl.spexerutil

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.ValueCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.breaswl.spexerutil.ieorg.BreathSphereGlobalLayoutUtil
import com.breaswl.spexerutil.ieorg.breathSphereSetupSystemBars
import com.breaswl.spexerutil.ieorg.presentation.app.BreathSphereApp
import com.breaswl.spexerutil.ieorg.presentation.pushhandler.BreathSpherePushHandler
import org.koin.android.ext.android.inject

class BreatheSphereActivity : AppCompatActivity() {
    lateinit var breathSpherePhoto: Uri
    var breathSphereFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    val breathSphereTakeFile = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        breathSphereFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
    }

    val breathSphereTakePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            breathSphereFilePathFromChrome?.onReceiveValue(arrayOf(breathSpherePhoto))
        } else {
            breathSphereFilePathFromChrome?.onReceiveValue(null)
        }
    }

    private val breathSpherePushHandler by inject<BreathSpherePushHandler>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        breathSphereSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_breathe_sphere)
        val breathSphereRootView = findViewById<View>(android.R.id.content)
        BreathSphereGlobalLayoutUtil().breathSphereAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(breathSphereRootView) { breathSphereView, breathSphereInsets ->
            val breathSphereSystemBars = breathSphereInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val breathSphereDisplayCutout = breathSphereInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val breathSphereIme = breathSphereInsets.getInsets(WindowInsetsCompat.Type.ime())


            val breathSphereTopPadding = maxOf(breathSphereSystemBars.top, breathSphereDisplayCutout.top)
            val breathSphereLeftPadding = maxOf(breathSphereSystemBars.left, breathSphereDisplayCutout.left)
            val breathSphereRightPadding = maxOf(breathSphereSystemBars.right, breathSphereDisplayCutout.right)
            window.setSoftInputMode(BreathSphereApp.breathSphereInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "ADJUST PUN")
                val breathSphereBottomInset = maxOf(breathSphereSystemBars.bottom, breathSphereDisplayCutout.bottom)

                breathSphereView.setPadding(breathSphereLeftPadding, breathSphereTopPadding, breathSphereRightPadding, 0)

                breathSphereView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = breathSphereBottomInset
                }
            } else {
                Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "ADJUST RESIZE")

                val breathSphereBottomInset = maxOf(breathSphereSystemBars.bottom, breathSphereDisplayCutout.bottom, breathSphereIme.bottom)

                breathSphereView.setPadding(breathSphereLeftPadding, breathSphereTopPadding, breathSphereRightPadding, 0)

                breathSphereView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = breathSphereBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "Activity onCreate()")
        breathSpherePushHandler.breathSphereHandlePush(intent.extras)
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            breathSphereSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        breathSphereSetupSystemBars()
    }
}
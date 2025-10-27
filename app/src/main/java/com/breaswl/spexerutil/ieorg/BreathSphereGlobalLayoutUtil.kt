package com.breaswl.spexerutil.ieorg

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.breaswl.spexerutil.ieorg.presentation.app.BreathSphereApp

class BreathSphereGlobalLayoutUtil {

    private var breathSphereMChildOfContent: View? = null
    private var breathSphereUsableHeightPrevious = 0

    fun breathSphereAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        breathSphereMChildOfContent = content.getChildAt(0)

        breathSphereMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val breathSphereUsableHeightNow = breathSphereComputeUsableHeight()
        if (breathSphereUsableHeightNow != breathSphereUsableHeightPrevious) {
            val breathSphereUsableHeightSansKeyboard = breathSphereMChildOfContent?.rootView?.height ?: 0
            val breathSphereHeightDifference = breathSphereUsableHeightSansKeyboard - breathSphereUsableHeightNow

            if (breathSphereHeightDifference > (breathSphereUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(BreathSphereApp.breathSphereInputMode)
            } else {
                activity.window.setSoftInputMode(BreathSphereApp.breathSphereInputMode)
            }
//            mChildOfContent?.requestLayout()
            breathSphereUsableHeightPrevious = breathSphereUsableHeightNow
        }
    }

    private fun breathSphereComputeUsableHeight(): Int {
        val r = Rect()
        breathSphereMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}
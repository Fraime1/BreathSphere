package com.breaswl.spexerutil.ieorg.domain.usecases

import android.util.Log
import com.breaswl.spexerutil.ieorg.data.repo.BreathSphereRepository
import com.breaswl.spexerutil.ieorg.data.utils.BreathSpherePushToken
import com.breaswl.spexerutil.ieorg.data.utils.BreathSphereSystemService
import com.breaswl.spexerutil.ieorg.domain.model.BreathSphereEntity
import com.breaswl.spexerutil.ieorg.domain.model.BreathSphereParam
import com.breaswl.spexerutil.ieorg.presentation.app.BreathSphereApp

class BreathSphereGetAllUseCase(
    private val breathSphereRepository: BreathSphereRepository,
    private val breathSphereSystemService: BreathSphereSystemService,
    private val breathSpherePushToken: BreathSpherePushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : BreathSphereEntity?{
        val params = BreathSphereParam(
            breathSphereLocale = breathSphereSystemService.breathSphereGetLocale(),
            breathSpherePushToken = breathSpherePushToken.breathSphereGetToken(),
            breathSphereAfId = breathSphereSystemService.breathSphereGetAppsflyerId()
        )
//        breathSphereSystemService.bubblePasswrodGetGaid()
        Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "Params for request: $params")
        return breathSphereRepository.breathSphereGetClient(params, conversion)
    }



}
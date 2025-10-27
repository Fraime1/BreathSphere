package com.breaswl.spexerutil.ieorg.presentation.di

import com.breaswl.spexerutil.ieorg.data.repo.BreathSphereRepository
import com.breaswl.spexerutil.ieorg.data.shar.BreathSphereSharedPreference
import com.breaswl.spexerutil.ieorg.data.utils.BreathSpherePushToken
import com.breaswl.spexerutil.ieorg.data.utils.BreathSphereSystemService
import com.breaswl.spexerutil.ieorg.domain.usecases.BreathSphereGetAllUseCase
import com.breaswl.spexerutil.ieorg.presentation.pushhandler.BreathSpherePushHandler
import com.breaswl.spexerutil.ieorg.presentation.ui.load.BreathSphereLoadViewModel
import com.breaswl.spexerutil.ieorg.presentation.ui.view.BreathSphereViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val breathSphereModule = module {
    factory {
        BreathSpherePushHandler()
    }
    single {
        BreathSphereRepository()
    }
    single {
        BreathSphereSharedPreference(get())
    }
    factory {
        BreathSpherePushToken()
    }
    factory {
        BreathSphereSystemService(get())
    }
    factory {
        BreathSphereGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        BreathSphereViFun(get())
    }
    viewModel {
        BreathSphereLoadViewModel(get(), get(), get())
    }
}
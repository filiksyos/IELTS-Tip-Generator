package com.example.presentation.DI

import android.content.Context
import com.example.data.Repository
import com.example.data.RepositoryInterface
import com.example.data.preferences.PreferencesManager
import com.example.domain.GetDashboardItemsUseCase
import com.example.domain.RefreshQueriesUseCase
import com.example.presentation.viewModel.DashboardViewModel
import com.example.presentation.viewModel.OnboardingViewModel
import com.example.presentation.viewModel.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Preferences Manager
    single { PreferencesManager(androidContext()) }

    // Repository binding
    single<RepositoryInterface> { Repository(get()) }

    // UseCases
    factory { GetDashboardItemsUseCase(get()) }
    factory { RefreshQueriesUseCase(get()) }

    // ViewModels
    viewModel { DashboardViewModel(get(), get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
}


package com.example.presentation.DI

import com.example.data.Repository
import com.example.data.RepositoryInterface
import com.example.domain.GetDashboardItemsUseCase
import com.example.presentation.viewModel.DashboardViewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {

    // Repository binding
    single<RepositoryInterface> { Repository() }

    // UseCase
    factory { GetDashboardItemsUseCase(get()) }

    // ViewModel binding
    viewModel { DashboardViewModel(get()) }
}


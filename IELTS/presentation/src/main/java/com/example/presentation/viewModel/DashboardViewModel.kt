package com.example.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DashboardItems
import com.example.domain.DashboardCategoryType
import com.example.domain.GetDashboardItemsUseCase
import kotlinx.coroutines.launch

class DashboardViewModel(private val getDashboardItemsUseCase: GetDashboardItemsUseCase) : ViewModel() {

    private val _dashboardItems = MutableLiveData<Map<DashboardCategoryType, List<DashboardItems>>>()
    val dashboardItems: LiveData<Map<DashboardCategoryType, List<DashboardItems>>> get() = _dashboardItems

    fun loadDashboardItems() {
        viewModelScope.launch {
            val itemsMap = DashboardCategoryType.entries.associateWith { categoryType ->
                getDashboardItemsUseCase.invoke(categoryType)
            }
            _dashboardItems.postValue(itemsMap)
        }
    }
}

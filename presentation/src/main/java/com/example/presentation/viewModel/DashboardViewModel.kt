package com.example.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DashboardItems
import com.example.data.RepositoryInterface
import com.example.domain.DashboardCategoryType
import com.example.domain.GetDashboardItemsUseCase
import com.example.domain.DashboardItemsObserverUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getDashboardItemsUseCase: GetDashboardItemsUseCase,
    private val dashboardItemsObserverUseCase: DashboardItemsObserverUseCase,
    private val repository: RepositoryInterface
) : ViewModel() {

    private val _dashboardItems = MutableLiveData<Map<DashboardCategoryType, List<DashboardItems>>>()
    val dashboardItems: LiveData<Map<DashboardCategoryType, List<DashboardItems>>> get() = _dashboardItems
    
    // Remaining credits
    private val _remainingCredits = MutableLiveData<Int>()
    val remainingCredits: LiveData<Int> get() = _remainingCredits

    init {
        // Observe the repository's StateFlow through the use case
        viewModelScope.launch {
            dashboardItemsObserverUseCase.observeDashboardItems().collectLatest { itemsMap ->
                // Convert from DashboardCategory to DashboardCategoryType
                val convertedMap = itemsMap.entries.associate { (category, items) ->
                    val categoryType = when (category) {
                        com.example.data.DashboardCategory.READING -> DashboardCategoryType.READING
                        com.example.data.DashboardCategory.LISTENING -> DashboardCategoryType.LISTENING
                        com.example.data.DashboardCategory.WRITING -> DashboardCategoryType.WRITING
                        com.example.data.DashboardCategory.SPEAKING -> DashboardCategoryType.SPEAKING
                    }
                    categoryType to items
                }
                _dashboardItems.postValue(convertedMap)
            }
        }
        
        // Observe remaining credits
        viewModelScope.launch {
            repository.remainingCreditsFlow.collectLatest { credits ->
                _remainingCredits.postValue(credits)
            }
        }
    }

    fun loadDashboardItems() {
        viewModelScope.launch {
            val itemsMap = DashboardCategoryType.entries.associateWith { categoryType ->
                getDashboardItemsUseCase.invoke(categoryType)
            }
            _dashboardItems.postValue(itemsMap)
        }
    }
}

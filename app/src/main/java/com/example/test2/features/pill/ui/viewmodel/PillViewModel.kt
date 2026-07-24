package com.example.test2.features.pill.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.test2.features.pill.data.local.PillEntity
import com.example.test2.features.pill.data.repository.PillRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class PillViewModel(
    private val repository: PillRepository
) : ViewModel() {

    private val _pills = MutableStateFlow<List<PillEntity>>(emptyList())
    val pills: StateFlow<List<PillEntity>> = _pills.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        loadPills()
    }

    private fun loadPills() {
        viewModelScope.launch {
            repository.getPills()
                .collect { pillList ->

                    _pills.value = pillList
                    _loading.value = false
                }
        }
    }

    //It will refresh authomatically the view.
    fun addPill(pillName: String) {
        viewModelScope.launch {
            repository.insert(
                PillEntity(
                    id = 0L,
                    name = pillName
                )
            )
        }
    }

    //It will refresh authomatically the view.
    fun deletePill(pill: PillEntity) {
        viewModelScope.launch {
            repository.delete(pill)
        }
    }


    class Factory(
        private val repository: PillRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PillViewModel(repository) as T
        }
    }
}

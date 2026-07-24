package com.example.test2.features.weight.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.test2.features.weight.data.local.WeightEntity
import com.example.test2.features.weight.data.repository.WeightRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime


class WeightViewModel(
    private val repository: WeightRepository
) : ViewModel() {

    private val _weights = MutableStateFlow<List<WeightEntity>>(emptyList())
    val weights: StateFlow<List<WeightEntity>> = _weights.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        loadWeights()
    }

    private fun loadWeights() {
        viewModelScope.launch {
            repository.getAll()
                .collect { weightList ->

                    _weights.value = weightList
                    _loading.value = false
                }
        }
    }

    //It will refresh authomatically the view.
    fun addWeight(weight: Float) {
        viewModelScope.launch {
            repository.insert(
                WeightEntity(
                    id = 0L,
                    date = OffsetDateTime.now(),
                    weight = weight
                )
            )
        }
    }

    //It will refresh authomatically the view.
    fun deleteWeight(weight: WeightEntity) {
        viewModelScope.launch {
            repository.delete(weight)
        }
    }


    class Factory(
        private val repository: WeightRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WeightViewModel(repository) as T
        }
    }
}

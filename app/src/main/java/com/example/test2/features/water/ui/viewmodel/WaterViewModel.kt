package com.example.test2.features.water.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.test2.features.water.data.local.WaterEntity
import com.example.test2.features.water.data.repository.WaterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import kotlin.collections.emptyList


class WaterViewModel(
    private val repository: WaterRepository
) : ViewModel() {

    private val _waters = MutableStateFlow<List<WaterEntity>>(emptyList())
    val waters: StateFlow<List<WaterEntity>> = _waters.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        loadWaters()
    }

    private fun loadWaters() {
        viewModelScope.launch {
            repository.getAll()
                .collect { waterList ->

                    _waters.value = waterList
                    _loading.value = false
                }
        }
    }

    //It will refresh authomatically the view.
    fun addWaterIntake(volume: Float) {
        viewModelScope.launch {
            repository.insert(
                WaterEntity(
                    id = 0L,
                    date = OffsetDateTime.now(),
                    volume = volume
                )
            )
        }
    }

    //It will refresh authomatically the view.
    fun deleteWater(water: WaterEntity) {
        viewModelScope.launch {
            repository.delete(water)
        }
    }


    class Factory(
        private val repository: WaterRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WaterViewModel(repository) as T
        }
    }
}

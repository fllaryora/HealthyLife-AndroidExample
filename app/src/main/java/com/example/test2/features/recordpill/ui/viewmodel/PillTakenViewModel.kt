package com.example.test2.features.recordpill.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.test2.features.pill.data.local.PillEntity
import com.example.test2.features.recordpill.data.local.PillTakenEntity
import com.example.test2.features.recordpill.data.repository.PillTakenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.emptyList


class PillTakenViewModel(
    private val repository: PillTakenRepository,
    private val pill :PillEntity
) : ViewModel() {

    private val _pillsTaken = MutableStateFlow<List<PillTakenEntity>>(emptyList())
    val pillsTaken: StateFlow<List<PillTakenEntity>> = _pillsTaken.asStateFlow()


    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        loadPillsTaken()
    }

    private fun loadPillsTaken() {
        viewModelScope.launch {
            repository.getAll()
                .collect { pillsTaken ->

                    _pillsTaken.value = pillsTaken
                    _loading.value = false
                }
        }
    }

    //It will refresh authomatically the view.
    fun addPillTaken() {
        viewModelScope.launch {
            repository.insert(
                PillTakenEntity.create(pillEntityAsociated = pill)
            )
        }
    }

    //It will refresh authomatically the view.
    fun deletePillTaken(pillTaken: PillTakenEntity) {
        viewModelScope.launch {
            repository.delete(pillTaken)
        }
    }


    class Factory(
        private val repository: PillTakenRepository,
        private val pill :PillEntity
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PillTakenViewModel(repository, pill) as T
        }
    }
}

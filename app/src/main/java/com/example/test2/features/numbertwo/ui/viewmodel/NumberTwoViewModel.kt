package com.example.test2.features.numbertwo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.test2.features.numbertwo.data.local.NumberTwoEntity
import com.example.test2.features.numbertwo.data.repository.NumberTwoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime


class NumberTwoViewModel(
    private val repository: NumberTwoRepository
) : ViewModel() {

    private val _wc = MutableStateFlow<List<NumberTwoEntity>>(emptyList())
    val wc: StateFlow<List<NumberTwoEntity>> = _wc.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        loadNumberTwoVisits()
    }

    private fun loadNumberTwoVisits() {
        viewModelScope.launch {
            repository.getAll()
                .collect { wcList ->

                    _wc.value = wcList
                    _loading.value = false
                }
        }
    }

    //It will refresh authomatically the view.
    fun addWCVisit() {
        viewModelScope.launch {
            repository.insert(
                NumberTwoEntity(
                    id = 0L,
                    date = OffsetDateTime.now(),
                )
            )
        }
    }

    //It will refresh authomatically the view.
    fun deleteWC(numberTwo: NumberTwoEntity) {
        viewModelScope.launch {
            repository.delete(numberTwo)
        }
    }


    class Factory(
        private val repository: NumberTwoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NumberTwoViewModel(repository) as T
        }
    }
}

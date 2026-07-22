package com.example.test2.features.recordactivity.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.recordactivity.data.local.ActivityTakenEntity
import com.example.test2.features.recordactivity.data.repository.ActivityTakenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.emptyList


class ActivityTakenViewModel(
    private val repository: ActivityTakenRepository,
    private val activity : DailyActivityEntity
) : ViewModel() {

    private val _activitiesTaken = MutableStateFlow<List<ActivityTakenEntity>>(emptyList())
    val activitiesTaken: StateFlow<List<ActivityTakenEntity>> = _activitiesTaken.asStateFlow()


    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        loadPillsTaken()
    }

    private fun loadPillsTaken() {
        viewModelScope.launch {
            repository.getAll()
                .collect { activitiesTaken ->

                    _activitiesTaken.value = activitiesTaken
                    _loading.value = false
                }
        }
    }

    //It will refresh authomatically the view.
    fun addActivityTaken() {
        viewModelScope.launch {
            repository.insert(
                ActivityTakenEntity.create(activityEntityAsociated = activity)
            )
        }
    }

    //It will refresh authomatically the view.
    fun deleteActivityTaken(activity: ActivityTakenEntity) {
        viewModelScope.launch {
            repository.delete(activity)
        }
    }


    class Factory(
        private val repository: ActivityTakenRepository,
        private val activity : DailyActivityEntity
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ActivityTakenViewModel(repository, activity) as T
        }
    }
}

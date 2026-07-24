package com.example.test2.features.dailyactivity.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.data.entities.enums.toMask
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.dailyactivity.data.repository.ActivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActivityViewModel(
    private val repository: ActivityRepository
) : ViewModel() {

    private val _activities = MutableStateFlow<List<DailyActivityEntity>>(emptyList())
    val activities: StateFlow<List<DailyActivityEntity>> = _activities.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        loadActivities()
    }

    private fun loadActivities() {
        viewModelScope.launch {
            repository.getActivities()
                .collect { activityList ->
                    _activities.value = activityList
                    _loading.value = false
                }
        }
    }

    //It will refresh authomatically the view.
    fun addActivity( activityName: String,
                     activityHour: Int,
                     activityMinute: Int,
                     selectedDays: Set<DaysOfWeekEnum> ,
                     activityTypeOfRecorder : TypeofRecorder,
                     activityIsAlarmEnabled:Boolean = false ) {
        viewModelScope.launch {
            val activityDaysOfWeek = selectedDays.toMask()
            repository.insert(
                DailyActivityEntity(
                    id = 0L,
                    name = activityName,
                    hour = activityHour,
                    minute = activityMinute,
                    daysOfWeek = activityDaysOfWeek,
                    typeOfRecorder = activityTypeOfRecorder.value,
                    isAlarmEnabled = activityIsAlarmEnabled
                )
            )
        }
    }

    //It will refresh authomatically the view.
    fun deleteActivity(activity: DailyActivityEntity) {
        viewModelScope.launch {
            repository.delete(activity)
        }
    }


    class Factory(
        private val repository: ActivityRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ActivityViewModel(repository) as T
        }
    }
}

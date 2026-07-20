package com.example.test2.data.dao.helpers

import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest


object FlatActivities {
    /***
     * This function duplicate each activity by day when it applies
     */
    fun flatActivities(activityList:List<DailyActivityEntity>,
                       currentDayOfWeek: DaysOfWeekEnum
    ): List<DailyActivityEntity> {
        return currentDayOfWeek.getList()
                .flatMap { dayIterator:DaysOfWeekEnum  ->
                    activityList
                        .filter {  thisActivity:DailyActivityEntity->
                            dayIterator.applyOn(thisActivity.daysOfWeek)
                        }
                        .map {  thisActivity:DailyActivityEntity->
                            thisActivity.copy(daysOfWeek = dayIterator.value) }
                }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun flatActivitiesFlow(
        activitiesFlow: Flow<List<DailyActivityEntity>>,
        currentDayOfWeek: DaysOfWeekEnum,
        ioDispatcher: CoroutineDispatcher
    ): Flow<List<DailyActivityEntity>> =
        activitiesFlow.mapLatest { activityList ->
            currentDayOfWeek.getList()
                .flatMap { dayIterator ->
                    activityList
                        .filter { activity ->
                            dayIterator.applyOn(activity.daysOfWeek)
                        }
                        .map { activity ->
                            activity.copy(daysOfWeek = dayIterator.value)
                        }
                }
        }.flowOn(ioDispatcher)
}
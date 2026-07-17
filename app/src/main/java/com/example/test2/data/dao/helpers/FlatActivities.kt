package com.example.test2.data.dao.helpers

import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity


object FlatActivities {
    /***
     * This function duplicate each activity by day when it applies
     */
    fun flatActivities(activityList:List<DailyActivityEntity>,
                       currentDayOfWeek: DaysOfWeekEnum
    ): List<DailyActivityEntity> {
        val flattenedList:MutableList<DailyActivityEntity> = mutableListOf()
        for(dayIterator:DaysOfWeekEnum in currentDayOfWeek.getList()) {
            for(thisActivity:DailyActivityEntity in activityList) {
                if(dayIterator.applyOn(thisActivity.daysOfWeek)) {
                    flattenedList.add(thisActivity.copy(daysOfWeek = dayIterator.value))
                }
            }
        }
        return flattenedList
    }
}
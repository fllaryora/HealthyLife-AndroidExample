package com.example.test2.data.dao.helpers

import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.implementations.DailyActivity


object FlatActivities {
    /***
     * This function duplicate each activity by day when it applies
     */
    fun flatActivities(activityList:List<DailyActivity>,
                       currentDayOfWeek: DaysOfWeekEnum
    ): List<DailyActivity> {
        val flattenedList:MutableList<DailyActivity> = mutableListOf()
        for(dayIterator:DaysOfWeekEnum in currentDayOfWeek.getList()) {
            for(thisActivity:DailyActivity in activityList) {
                if(dayIterator.applyOn(thisActivity.daysOfWeek)) {
                    flattenedList.add(thisActivity.copy(daysOfWeek = dayIterator.value))
                }
            }
        }
        return flattenedList
    }
}
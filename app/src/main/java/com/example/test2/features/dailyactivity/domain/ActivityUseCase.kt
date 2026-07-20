package com.example.test2.features.dailyactivity.domain

import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.features.dailyactivity.data.local.ActivityDAO
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity

interface ActivityUseCase {

    fun initialize( mActivityDAO: ActivityDAO)
    /**
     * Finds the next activity based on the current date and time.
     *
     * @param currentHour Current hour in 24-hour format.
     * @param currentMinute Current minute.
     * @param currentDayOfWeek Current day of the week.
     *
     * @return The next scheduled activity or null if none exists.
     */
    fun getNextActivity(
        currentHour: Int,
        currentMinute: Int,
        currentDayOfWeek: DaysOfWeekEnum
    ): DailyActivityEntity?

    /**
     * Calculates the next activity from a provided list of activities.
     *
     * @param currentHour Current hour in 24-hour format.
     * @param currentMinute Current minute.
     * @param currentDayOfWeek Current day of the week.
     * @param activityList List of candidate activities.
     *
     * @return The next matching activity or null if the list is empty.
     */
    fun getNextActivityFromList(
        currentHour: Int,
        currentMinute: Int,
        currentDayOfWeek: DaysOfWeekEnum,
        activityList: List<DailyActivityEntity>
    ): DailyActivityEntity?
}
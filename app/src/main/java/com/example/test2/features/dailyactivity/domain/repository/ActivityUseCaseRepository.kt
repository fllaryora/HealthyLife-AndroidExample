package com.example.test2.features.dailyactivity.domain.repository

import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.dailyactivity.data.repository.ActivityRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

/**
 * Contract for DailyActivity data access operations.
 *
 * Provides CRUD operations and utility methods for querying
 * and calculating the next scheduled activity.
 */
interface ActivityUseCaseRepository {

    /**
     * Initializes the DAO with the ObjectBox box instance.
     *
     * Must be called before any other operation.
     *
     * @param box ObjectBox container for DailyActivityEntity persistence.
     */
    fun initialize(activityRepository: ActivityRepository, dispatcher: CoroutineDispatcher)


    /**
     * Finds the next activity based on the current date and time.
     *
     * @param currentHour Current hour in 24-hour format.
     * @param currentMinute Current minute.
     * @param currentDayOfWeek Current day of the week.
     *
     * @return The next scheduled activity or null if none exists.
     */
    suspend fun getNextActivity(
        currentHour: Int,
        currentMinute: Int,
        currentDayOfWeek: DaysOfWeekEnum
    ): Flow<DailyActivityEntity?>

    suspend fun getNextActivityFromList(currentHour: Int, currentMinute : Int,
                                         currentDayOfWeek: DaysOfWeekEnum,
                                        activityListFlow:Flow<List<DailyActivityEntity>>): Flow<DailyActivityEntity?>
}
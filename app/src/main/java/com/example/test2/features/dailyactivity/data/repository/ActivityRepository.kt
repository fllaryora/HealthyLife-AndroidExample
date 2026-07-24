package com.example.test2.features.dailyactivity.data.repository

import com.example.test2.features.dailyactivity.data.local.ActivityDAO
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

/**
 * Contract for DailyActivity data access operations.
 *
 * Provides CRUD operations and utility methods for querying
 * and calculating the next scheduled activity.
 */
interface ActivityRepository {

    /**
     * Initializes the DAO with the ObjectBox box instance.
     *
     * Must be called before any other operation.
     *
     * @param box ObjectBox container for DailyActivityEntity persistence.
     */
    fun initialize(activityDAO: ActivityDAO, dispatcher: CoroutineDispatcher)

    /**
     * Inserts a new activity into the database.
     *
     * @param newActivity Activity to be persisted.
     * @return The generated database ID.
     * @throws Exception If a unique constraint violation occurs.
     */
    suspend fun insert(newActivity: DailyActivityEntity): Long

    /**
     * Deletes the specified activity.
     *
     * @param activity Activity to remove.
     * @return true if the activity was removed successfully,
     * false if no matching entity was found.
     */
    suspend fun delete(activity: DailyActivityEntity): Boolean

    /**
     * Retrieves all stored activities.
     *
     * Activities are expected to be returned ordered
     * according to the DAO implementation.
     *
     * @return List of all activities.
     */
    suspend fun getActivities(): Flow<List<DailyActivityEntity>>

    /**
     * Removes all activities from the database.
     */
    suspend fun deleteAll(): Unit

}
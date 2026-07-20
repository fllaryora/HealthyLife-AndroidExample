package com.example.test2.features.dailyactivity.data.local

import com.example.test2.data.entities.enums.DaysOfWeekEnum
import io.objectbox.Box

/**
 * Contract for DailyActivity data access operations.
 *
 * Provides CRUD operations and utility methods for querying
 * and calculating the next scheduled activity.
 */
interface ActivityDAO {

    /**
     * Initializes the DAO with the ObjectBox box instance.
     *
     * Must be called before any other operation.
     *
     * @param box ObjectBox container for DailyActivityEntity persistence.
     */
    fun initialize(box: Box<DailyActivityEntity>)

    /**
     * Inserts a new activity into the database.
     *
     * @param newActivity Activity to be persisted.
     * @return The generated database ID.
     * @throws Exception If a unique constraint violation occurs.
     */
    fun insert(newActivity: DailyActivityEntity): Long

    /**
     * Deletes the specified activity.
     *
     * @param activity Activity to remove.
     * @return true if the activity was removed successfully,
     * false if no matching entity was found.
     */
    fun delete(activity: DailyActivityEntity): Boolean

    /**
     * Retrieves all stored activities.
     *
     * Activities are expected to be returned ordered
     * according to the DAO implementation.
     *
     * @return List of all activities.
     */
    fun getActivities(): List<DailyActivityEntity>


    /**
     * Removes all activities from the database.
     */
    fun deleteAll()

    fun getBox( ):Box<DailyActivityEntity>
}
package com.example.test2.features.water.data.local


import com.example.test2.data.dao.behaviors.TimelineableDAO

import io.objectbox.Box
import java.time.OffsetDateTime

/**
 * Data Access Object (DAO) responsible for managing [WaterEntity] intake records.
 *
 * Extends [TimelineableDAO] to provide timeline-related operations
 * and adds water-specific persistence and query methods.
 */
interface WaterDAO : TimelineableDAO<WaterEntity> {

    /**
     * Initializes the DAO with the ObjectBox storage instance.
     *
     * This method must be called before performing any database operation.
     *
     * @param box ObjectBox box used to store and retrieve [WaterEntity] entities.
     */
    fun initialize(box: Box<WaterEntity>)

    /**
     * Persists a new water intake record.
     *
     * @param water The water intake entry to be inserted.
     *
     * @return The identifier assigned to the newly stored entity.
     */
    fun insert(water: WaterEntity): Long

    /**
     * Retrieves all water intake records for the specified day.
     *
     * The comparison is performed using the date portion of the
     * provided timestamp. Time information is ignored.
     *
     * Results are returned in chronological order.
     *
     * @param day Day for which water intake entries should be retrieved.
     *
     * @return A list containing all water intake records registered
     * on the specified day.
     */
    fun getIntakesByDay(day: OffsetDateTime): List<WaterEntity>

    /**
     * Retrieves a paginated list of water intake records.
     *
     * This method is typically used by timeline screens in order to
     * progressively load historical data without reading the entire
     * database contents.
     *
     * @param offset Number of records to skip.
     * @param limit Maximum number of records to return.
     *
     * @return A list of [WaterEntity] entities ordered according to the DAO
     * implementation.
     */
    fun getWaters(
        offset: Long,
        limit: Long
    ): List<WaterEntity>

    /**
     * Deletes all stored water intake records.
     *
     * Use with caution, as this operation cannot be undone.
     */
    fun deleteAll()

    /**
     * Retrieves every stored water intake record.
     *
     * This method is intended for debugging, testing or maintenance
     * scenarios where pagination is not required.
     *
     * @return A list containing all stored [WaterEntity] entities.
     */
    fun getAll(): List<WaterEntity>

    /**
     * Returns the underlying ObjectBox box used by the DAO.
     *
     * Primarily intended for advanced operations, testing, or
     * infrastructure code that requires direct access to ObjectBox.
     *
     * @return The ObjectBox [Box] instance backing this DAO.
     */
    fun getBox(): Box<WaterEntity>
}
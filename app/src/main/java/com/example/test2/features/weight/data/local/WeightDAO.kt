package com.example.test2.features.weight.data.local

import com.example.test2.data.dao.behaviors.TimelineableDAO
import io.objectbox.Box
import io.objectbox.query.Query


/**
 * Data Access Object (DAO) for managing [WeightEntity] instances.
 *
 * Extends [TimelineableDAO] to provide timeline-related operations
 * and adds weight-specific persistence methods.
 */

interface WeightDAO : TimelineableDAO<WeightEntity> {

    /**
     * Initializes the DAO with the ObjectBox storage instance.
     *
     * This method must be called before performing any CRUD operation.
     *
     * @param box ObjectBox box used to store and retrieve [WeightEntity] instances.
     */

    fun initialize(box: Box<WeightEntity>) : Unit

    /**
     * Persists a new weight record.
     *
     * @param weightEntity The weight entity to be inserted.
     * @return The identifier assigned to the newly stored entity.
     */


    fun insert(weightEntity: WeightEntity) : Long

    /**
     * Retrieves a paginated list of weight records together with the
     * first recorded weight in the selected range.
     *
     * This is typically used to calculate progress or display a
     * baseline value alongside the retrieved measurements.
     *
     * @param offset Number of records to skip.
     * @param limit Maximum number of records to return.
     * @return A [Pair] containing:
     * - A list of [WeightEntity] records.
     * - The first recorded weight value, or `null` if no records exist.
     */

    fun getWeightsAndFirstDay(offset: Long, limit: Long): Pair<List<WeightEntity>, Float?>


    /**
     * Retrieves a paginated list of weight records.
     *
     * @param offset Number of records to skip.
     * @param limit Maximum number of records to return.
     * @return A list of [WeightEntity] instances ordered according
     * to the DAO implementation.
     */

    fun getWeights(offset: Long, limit: Long): List<WeightEntity>

    /**
     * Deletes all stored weight records.
     *
     * Use with caution, as this operation cannot be undone.
     */

    fun deleteAll()

    /**
     * Retrieves all stored weight records.
     *
     * @return A list containing every stored [WeightEntity].
     */

    fun getAll() : List<WeightEntity>

    fun getBox(): Box<WeightEntity>

}
package com.example.test2.features.weight.data.repository

import com.example.test2.features.weight.data.local.WeightDAO
import com.example.test2.features.weight.data.local.WeightEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface WeightRepository {

    /**
     * Initializes the DAO with the ObjectBox storage instance.
     *
     * This method must be called before performing any CRUD operation.
     *
     * @param mWeightDAO with ObjectBox box used to store and retrieve [WeightEntity] instances.
     */

    fun initialize(mWeightDAO: WeightDAO, dispatcher: CoroutineDispatcher) : Unit

    /**
     * Persists a new weight record.
     *
     * @param weightEntity The weight entity to be inserted.
     * @return The identifier assigned to the newly stored entity.
     */

    suspend fun insert(weightEntity: WeightEntity) : Long

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

    suspend fun getWeightsAndFirstDay(offset: Long, limit: Long): Flow<Pair<List<WeightEntity>, Float?>>


    /**
     * Retrieves a paginated list of weight records.
     *
     * @param offset Number of records to skip.
     * @param limit Maximum number of records to return.
     * @return A list of [WeightEntity] instances ordered according
     * to the DAO implementation.
     */

    suspend fun getWeights(offset: Long, limit: Long): Flow<List<WeightEntity>>

    /**
     * Deletes all stored weight records.
     *
     * Use with caution, as this operation cannot be undone.
     */

    suspend fun deleteAll(): Unit

    suspend fun delete(timelineable: WeightEntity) : Boolean

    /**
     * Retrieves all stored weight records.
     *
     * @return A list containing every stored [WeightEntity].
     */

    suspend fun getAll() : Flow<List<WeightEntity>>
}
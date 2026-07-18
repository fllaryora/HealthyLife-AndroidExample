package com.example.test2.features.numbertwo.data.repository


import com.example.test2.features.numbertwo.data.local.NumberTwoDAO
import com.example.test2.features.numbertwo.data.local.NumberTwoEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface NumberTwoRepository {
    fun initialize(mWaterDAO: NumberTwoDAO, dispatcher: CoroutineDispatcher) : Unit

    /**
     * Persists the supplied entity.
     *
     * @param numberTwoEntity the entity to be stored.
     * @return the database identifier assigned to the entity.
     * @throws Exception if the entity cannot be stored.
     */
    suspend fun insert(numberTwoEntity: NumberTwoEntity): Long


    /**
     * Retrieves a paginated collection of entities.
     *
     * Results are expected to be ordered according to the implementation
     * strategy.
     *
     * @param offset zero-based starting position.
     * @param limit maximum number of records to retrieve.
     * @return a list of entities.
     */
    suspend fun getNumberTwoList(
        offset: Long,
        limit: Long
    ): Flow<List<NumberTwoEntity>>

    /**
     * Removes every stored entity.
     */
    suspend fun deleteAll(): Unit

    /**
     * Retrieves all stored entities.
     *
     * @return all persisted entities.
     */
    suspend fun getAll(): Flow<List<NumberTwoEntity>>

    suspend fun delete(todoLineable: NumberTwoEntity): Boolean
}
package com.example.test2.features.pill.data.repository

import com.example.test2.features.pill.data.local.PillDAO
import com.example.test2.features.pill.data.local.PillEntity

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface PillRepository {

    fun initialize(mPillDAO: PillDAO, dispatcher: CoroutineDispatcher) : Unit

    /**
     * Inserts or updates a pill entity.
     *
     * @param pillEntity The pill entity to be stored.
     * @return The ID of the stored entity.
     * @throws Exception If the operation violates a unique constraint.
     */
    suspend fun insert(pillEntity: PillEntity): Long

    /**
     * Removes the specified pill entity.
     *
     * @param pillEntity The entity to delete.
     * @return `true` if the entity was successfully removed,
     * `false` if no entity exists with the given ID.
     */
    suspend fun delete(pillEntity: PillEntity): Boolean

    /**
     * Retrieves all stored pill entities.
     *
     * @return A list containing all pills.
     */
    suspend fun getPills(): Flow<List<PillEntity>>

    /**
     * Removes all pill entities from storage.
     */
    suspend fun deleteAll()
}
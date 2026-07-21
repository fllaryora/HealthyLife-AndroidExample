package com.example.test2.features.recordpill.data.repository

import com.example.test2.features.pill.data.local.PillEntity
import com.example.test2.features.recordpill.data.local.PillTakenDAO
import com.example.test2.features.recordpill.data.local.PillTakenEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface PillTakenRepository {
    /**
     * Initializes the DAO with the ObjectBox storage instance.
     *
     * This method must be called before performing any CRUD operation.
     *
     * @param mPillTakenDAO with ObjectBox box used to store and retrieve [PillTakenEntity] instances.
     */

    fun initialize(mPillTakenDAO: PillTakenDAO, dispatcher: CoroutineDispatcher) : Unit

    /**
     * Persists a new pill taken record.
     *
     * @param pillTakenEntity The weight entity to be inserted.
     * @return The identifier assigned to the newly stored entity.
     */

    suspend fun insert(pillTakenEntity: PillTakenEntity) : Long


    suspend fun deleteAll(): Unit

    suspend fun delete(todolineable: PillTakenEntity) : Boolean

    suspend fun deleteByPill(pillEntity: PillEntity) : Unit

    suspend fun getAll(): Flow<List<PillTakenEntity>>

    suspend fun getPillTaken(pillEntity: PillEntity, offset: Long, limit: Long): Flow<Pair<List<PillTakenEntity>, Float?>>

    suspend fun getPillTakenList(pillEntity: PillEntity, offset: Long, limit: Long): Flow<List<PillTakenEntity>>

    suspend fun getAllByPill(pillEntity: PillEntity): Flow<List<PillTakenEntity>>

}
package com.example.test2.features.water.data.repository
import com.example.test2.features.water.data.local.WaterDAO
import com.example.test2.features.water.data.local.WaterEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface WaterRepository {
    /**
     * Initializes the DAO with the ObjectBox storage instance.
     *
     * This method must be called before performing any CRUD operation.
     *
     * @param mWaterDAO with ObjectBox box used to store and retrieve [WaterEntity] instances.
     */

    fun initialize(mWaterDAO: WaterDAO, dispatcher: CoroutineDispatcher) : Unit

    /**
     * Persists a new water intake.
     *
     * @param waterEntity The water entity to be inserted.
     * @return The identifier assigned to the newly stored entity.
     */

    suspend fun insert(waterEntity: WaterEntity) : Long

    /**
     * Deletes all stored weight records.
     *
     * Use with caution, as this operation cannot be undone.
     */

    suspend fun deleteAll(): Unit

    suspend fun delete(timelineable: WaterEntity) : Boolean

    suspend fun getIntakesByDay( day : OffsetDateTime): Flow<List<WaterEntity>>

    suspend fun getWaters(offset: Long, limit: Long): Flow<List<WaterEntity>>

    suspend fun getAll() : Flow<List<WaterEntity>>
}
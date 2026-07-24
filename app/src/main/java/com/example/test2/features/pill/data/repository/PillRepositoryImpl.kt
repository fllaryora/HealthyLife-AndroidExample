package com.example.test2.features.pill.data.repository

import com.example.test2.features.pill.data.local.PillDAO
import com.example.test2.features.pill.data.local.PillEntity
import io.objectbox.Box
import io.objectbox.kotlin.flow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

object PillRepositoryImpl : PillRepository {
    private val invalidations = MutableSharedFlow<Unit>()

    private lateinit var  mPillDAO: PillDAO
    private lateinit var mPillEntityBox: Box<PillEntity>

    private lateinit var ioDispatcher: CoroutineDispatcher

    override fun initialize(
        mPillDAO: PillDAO,
        dispatcher: CoroutineDispatcher
    ) {
        this.mPillDAO = mPillDAO
        this.mPillEntityBox = mPillDAO.getBox()
        ioDispatcher = dispatcher
    }

    /**
     * Inserts or updates a pill entity.
     *
     * @param pillEntity The pill entity to be stored.
     * @return The ID of the stored entity.
     * @throws Exception If the operation violates a unique constraint.
     */
    override suspend fun insert(pillEntity: PillEntity): Long {
        return withContext(ioDispatcher) {
            val returnedValue = mPillDAO.insert(pillEntity)
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }

    /**
     * Removes the specified pill entity.
     *
     * @param pillEntity The entity to delete.
     * @return `true` if the entity was successfully removed,
     * `false` if no entity exists with the given ID.
     */
    override suspend fun delete(pillEntity: PillEntity): Boolean {
        return withContext(ioDispatcher) {
            val returnedValue = mPillDAO.delete(pillEntity)
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }

    /**
     * Retrieves all stored pill entities.
     *
     * @return A list containing all pills.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getPills(): Flow<List<PillEntity>> {
        return  mPillEntityBox.query().build().flow()
            .map { it.toList<PillEntity>() }
            .flowOn(ioDispatcher)
    }

    /**
     * Removes all pill entities from storage.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun deleteAll() {
        return withContext(ioDispatcher) {
            val returnedValue = mPillDAO.deleteAll()
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }
}
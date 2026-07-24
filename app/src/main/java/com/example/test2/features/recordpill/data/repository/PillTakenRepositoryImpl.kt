package com.example.test2.features.recordpill.data.repository

import com.example.test2.features.pill.data.local.PillEntity
import com.example.test2.features.recordpill.data.local.PillTakenDAO
import com.example.test2.features.recordpill.data.local.PillTakenEntity
import com.example.test2.features.recordpill.data.local.PillTakenEntity_
import io.objectbox.Box
import io.objectbox.kotlin.flow
import io.objectbox.query.Query
import io.objectbox.query.QueryBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext


/**
 * DAO for objectbox
 */
object PillTakenRepositoryImpl : PillTakenRepository {

    private val invalidations = MutableSharedFlow<Unit>()

    private lateinit var mPillTakenDAO: PillTakenDAO
    private lateinit var mPillTakenEntityBox: Box<PillTakenEntity>

    private lateinit var ioDispatcher: CoroutineDispatcher

    override fun initialize(mPillTakenDAO: PillTakenDAO, dispatcher: CoroutineDispatcher) : Unit {
        this.mPillTakenDAO = mPillTakenDAO
        this.mPillTakenEntityBox = mPillTakenDAO.getBox()
        ioDispatcher = dispatcher
    }

    private fun getPillTakenQuery(pillEntity: PillEntity): Query<PillTakenEntity> {
        return mPillTakenEntityBox.query()
            .equal(PillTakenEntity_.pillEntityId, pillEntity.id)
            .order(
                PillTakenEntity_.date, QueryBuilder.DESCENDING
                        or QueryBuilder.CASE_SENSITIVE  ).build()
    }

    override suspend fun insert(pillTakenEntity: PillTakenEntity) : Long {
        check(pillTakenEntity.pillEntity.targetId != 0L) {
            """
        PillTakenEntity.pillEntity relation is missing.

        exportPillId=${pillTakenEntity.exportPillId}
        targetId=${pillTakenEntity.pillEntity.targetId}

        Setting exportPillId does NOT initialize the ObjectBox relation.
        Use:

        pillTakenEntity.pillEntity.target = pillEntity

        or

        pillTakenEntity.pillEntity.targetId = pillEntity.id
        """.trimIndent()
        }
        return withContext(ioDispatcher) {
            val returnedValue = mPillTakenDAO.insert(pillTakenEntity)
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }

    // Tested OK
    override suspend fun deleteAll() {
        return withContext(ioDispatcher) {
            val returnedValue = mPillTakenDAO.deleteAll()
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }

    // Tested OK
    override suspend fun delete(todolineable: PillTakenEntity): Boolean {

        check(todolineable.pillEntity.targetId != 0L) {
            """
        PillTakenEntity.pillEntity relation is missing.

        exportPillId=${todolineable.exportPillId}
        targetId=${todolineable.pillEntity.targetId}

        Setting exportPillId does NOT initialize the ObjectBox relation.
        Use:

        pillTakenEntity.pillEntity.target = pillEntity

        or

        pillTakenEntity.pillEntity.targetId = pillEntity.id
        """.trimIndent()
        }
        return withContext(ioDispatcher) {
            val returnedValue = mPillTakenDAO.delete(todolineable)
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }

    //TEST OK
    override suspend fun deleteByPill(pillEntity: PillEntity) : Unit{
        return withContext(ioDispatcher) {
            val returnedValue = mPillTakenDAO.deleteByPill(pillEntity)
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }

    // Tested OK
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAll(): Flow<List<PillTakenEntity>> {
        return  mPillTakenEntityBox.query().build().flow()
            .map { it.toList<PillTakenEntity>() }
            .flowOn(ioDispatcher)
    }

    //TEST OK
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllByPill(pillEntity: PillEntity): Flow<List<PillTakenEntity>> {
        return  getPillTakenQuery(pillEntity).flow()
            .map { it.toList<PillTakenEntity>() }
            .flowOn(ioDispatcher)
    }

    override suspend fun getPillTaken(
        pillEntity: PillEntity,
        offset: Long,
        limit: Long
    ): Flow<Pair<List<PillTakenEntity>, Float?>> {
        return invalidations
            .onStart {
                emit(Unit)
            }
            .map {
                mPillTakenDAO.getPillTaken(
                    pillEntity,
                    offset,
                    limit
                )
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun getPillTakenList(
        pillEntity: PillEntity,
        offset: Long,
        limit: Long
    ): Flow<List<PillTakenEntity>> {
        return invalidations
            .onStart {
                emit(Unit)
            }
            .map {
                mPillTakenDAO.getPillTakenList(
                    pillEntity,
                    offset,
                    limit
                )
            }
            .flowOn(ioDispatcher)
    }


}
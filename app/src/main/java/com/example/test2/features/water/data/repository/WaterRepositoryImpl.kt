package com.example.test2.features.water.data.repository

import com.example.test2.data.converter.TimeConverter
import com.example.test2.features.water.data.local.WaterDAO
import com.example.test2.features.water.data.local.WaterEntity
import com.example.test2.features.water.data.local.WaterEntity_
import io.objectbox.Box
import io.objectbox.kotlin.flow
import io.objectbox.query.QueryBuilder
import io.objectbox.query.QueryCondition
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime

object  WaterRepositoryImpl : WaterRepository {

    private val invalidations = MutableSharedFlow<Unit>()

    private lateinit var mWaterDAO: WaterDAO
    private lateinit var mWaterEntityBox: Box<WaterEntity>

    private lateinit var ioDispatcher: CoroutineDispatcher

    override fun initialize(
        mWaterDAO: WaterDAO,
        dispatcher: CoroutineDispatcher
    ) {
        this.mWaterDAO = mWaterDAO
        this.mWaterEntityBox = mWaterDAO.getBox()
        ioDispatcher = dispatcher
    }

    override suspend fun insert(waterEntity: WaterEntity): Long {
        return withContext(ioDispatcher) {
            val returnedValue = mWaterDAO.insert(waterEntity)
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }

    override suspend fun deleteAll() {
        return withContext(ioDispatcher) {
            mWaterDAO.deleteAll()
            invalidations.emit(Unit)
            return@withContext
        }
    }

    override suspend fun delete(timelineable: WaterEntity): Boolean {
        return withContext(ioDispatcher) {
            val returnedValue = mWaterDAO.delete(timelineable)
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getIntakesByDay(day: OffsetDateTime): Flow<List<WaterEntity>> {
        val stringDate = TimeConverter.fromOffsetDate(day)!!
        val conditions: QueryCondition<WaterEntity> = WaterEntity_.date.startsWith(stringDate)

        return  mWaterEntityBox.query(
            conditions
        )
            .order(WaterEntity_.date, QueryBuilder.CASE_SENSITIVE ).build().flow()
            .map { it.toList<WaterEntity>() }
            .flowOn(ioDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getWaters(
        offset: Long,
        limit: Long
    ): Flow<List<WaterEntity>> {
        return invalidations
            .onStart { emit(Unit) }
            .flatMapLatest {
                flow {
                    emit(
                        mWaterDAO.getWaters(offset, limit)
                    )
                }
            }.flowOn(ioDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAll(): Flow<List<WaterEntity>> {
        return  mWaterEntityBox.query().build().flow()
            .map { it.toList<WaterEntity>() }
            .flowOn(ioDispatcher)
    }

}
package com.example.test2.features.weight.data.repository

import com.example.test2.features.weight.data.local.WeightDAO
import com.example.test2.features.weight.data.local.WeightEntity
import io.objectbox.Box
import io.objectbox.kotlin.flow
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

object  WeightRepositoryImpl: WeightRepository {

    private val invalidations = MutableSharedFlow<Unit>()

    private lateinit var mWeightDAO: WeightDAO
    private lateinit var mWeightEntityBox: Box<WeightEntity>

    private lateinit var ioDispatcher: CoroutineDispatcher

    override fun initialize(
        mWeightDAO: WeightDAO,
        dispatcher: CoroutineDispatcher
    ) {
        this.mWeightDAO = mWeightDAO
        this.mWeightEntityBox = mWeightDAO.getBox()
        ioDispatcher = dispatcher
    }

    //tested OK
    override suspend fun insert(weightEntity: WeightEntity): Long {
        return withContext(ioDispatcher) {
            val returnedValue = mWeightDAO.insert(weightEntity)
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getWeightsAndFirstDay(
        offset: Long,
        limit: Long
    ): Flow<Pair<List<WeightEntity>, Float?>> {
        return invalidations
            .onStart { emit(Unit) }
            .flatMapLatest {
            flow {
                emit(
                    mWeightDAO.getWeightsAndFirstDay(offset, limit)
                )
            }
        }.flowOn(ioDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getWeights(
        offset: Long,
        limit: Long
    ): Flow<List<WeightEntity>> {
        return invalidations
            .onStart {
                emit(Unit)
            }
            .map {
                mWeightDAO.getWeights(
                    offset,
                    limit
                )
            }
            .flowOn(ioDispatcher)



    }

    //tested OK
    override suspend fun deleteAll() {
        return withContext(ioDispatcher) {
            mWeightDAO.deleteAll()
            invalidations.emit(Unit)
            return@withContext
        }
    }

    //tested OK
    override suspend fun delete(timelineable: WeightEntity): Boolean {
        return withContext(ioDispatcher) {
            val returnedValue = mWeightDAO.delete(timelineable)
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }

    // tested OK
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAll(): Flow<List<WeightEntity>> {
        return  mWeightEntityBox.query().build().flow()
            .map { it.toList<WeightEntity>() }
            .flowOn(ioDispatcher)
    }

}
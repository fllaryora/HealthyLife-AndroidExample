package com.example.test2.features.weight.data.repository

import com.example.test2.features.weight.data.local.WeightDAO
import com.example.test2.features.weight.data.local.WeightEntity
import io.objectbox.Box
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import io.objectbox.kotlin.flow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.collections.toList

object  WeightRepositoryImpl: WeightRepository {

    private lateinit var mWeightDAO: WeightDAO
    private lateinit var mWeightEntityBox: Box<WeightEntity>

    override fun initialize(mWeightDAO: WeightDAO) {
        this.mWeightDAO = mWeightDAO
        this.mWeightEntityBox = mWeightDAO.getBox()

    }

    override suspend fun insert(weightEntity: WeightEntity): Long {
        return withContext(Dispatchers.IO) {
            return@withContext mWeightDAO.insert(weightEntity)
        }
    }

    override suspend fun getWeightsAndFirstDay(
        offset: Long,
        limit: Long
    ): Flow<Pair<List<WeightEntity>, Float?>> {
        return withContext(Dispatchers.IO) {
            flowOf(
                mWeightDAO.getWeightsAndFirstDay(offset, limit)
            )

        }
    }

    override suspend fun getWeights(
        offset: Long,
        limit: Long
    ): Flow<List<WeightEntity>> {
        return withContext(Dispatchers.IO) {
            flowOf(
                mWeightDAO.getWeights(offset, limit)
            )

        }
    }

    override suspend fun deleteAll() {
        return withContext(Dispatchers.IO) {
            return@withContext mWeightDAO.deleteAll()
        }
    }

    override suspend fun delete(timelineable: WeightEntity): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext mWeightDAO.delete(timelineable)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAll(): Flow<List<WeightEntity>> {
        return  mWeightEntityBox.query().build().flow()
            .map { it.toList<WeightEntity>() }
            .flowOn(Dispatchers.IO)
    }

}
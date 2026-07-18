package com.example.test2.features.numbertwo.data.repository

import com.example.test2.features.numbertwo.data.local.NumberTwoDAO
import com.example.test2.features.numbertwo.data.local.NumberTwoEntity
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

object NumberTwoRepositoryImpl : NumberTwoRepository {
    private val invalidations = MutableSharedFlow<Unit>()

    private lateinit var mNumberTwoDAO: NumberTwoDAO
    private lateinit var mNumberTwoEntityBox: Box<NumberTwoEntity>
    private lateinit var ioDispatcher: CoroutineDispatcher

    override fun initialize(
        mNumberTwoDAO: NumberTwoDAO,
        dispatcher: CoroutineDispatcher
    ) {
        this.mNumberTwoDAO = mNumberTwoDAO
        this.mNumberTwoEntityBox = mNumberTwoDAO.getBox()
        ioDispatcher = dispatcher
    }

    override suspend fun insert(numberTwoEntity: NumberTwoEntity): Long {
        return withContext(ioDispatcher) {
            val returnedValue = mNumberTwoDAO.insert(numberTwoEntity)
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getNumberTwoList(
        offset: Long,
        limit: Long
    ): Flow<List<NumberTwoEntity>> {
        return invalidations
            .onStart { emit(Unit) }
            .flatMapLatest {
                flow {
                    emit(
                        mNumberTwoDAO.getNumberTwoList(offset, limit)
                    )
                }
            }.flowOn(ioDispatcher)
    }

    override suspend fun deleteAll() {
        return withContext(ioDispatcher) {
            mNumberTwoDAO.deleteAll()
            invalidations.emit(Unit)
            return@withContext
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAll(): Flow<List<NumberTwoEntity>> {
        return  mNumberTwoEntityBox.query().build().flow()
            .map { it.toList<NumberTwoEntity>() }
            .flowOn(ioDispatcher)
    }

    override suspend fun delete(todoLineable: NumberTwoEntity): Boolean {
        return withContext(ioDispatcher) {
            val returnedValue = mNumberTwoDAO.delete(todoLineable)
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }
}
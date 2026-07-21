package com.example.test2.features.recordactivity.data.repository

import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.recordactivity.data.local.ActivityTakenDAO
import com.example.test2.features.recordactivity.data.local.ActivityTakenEntity
import com.example.test2.features.recordactivity.data.local.ActivityTakenEntity_
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
import kotlin.collections.toList

object ActivityTakenRepositoryImpl : ActivityTakenRepository {

    private val invalidations = MutableSharedFlow<Unit>()

    private lateinit var mActivityTakenDAO: ActivityTakenDAO
    private lateinit var mActivityTakenEntityBox: Box<ActivityTakenEntity>

    private lateinit var ioDispatcher: CoroutineDispatcher
    override fun initialize(
        mActivityTakenDAO: ActivityTakenDAO,
        dispatcher: CoroutineDispatcher
    ) {
        this.mActivityTakenDAO = mActivityTakenDAO
        ioDispatcher = dispatcher
        mActivityTakenEntityBox = this.mActivityTakenDAO.getBox()
    }

    private fun getActivityTakenQuery(dailyActivityEntity: DailyActivityEntity): Query<ActivityTakenEntity> {
        return mActivityTakenEntityBox.query()
            .equal(ActivityTakenEntity_.activityId, dailyActivityEntity.id)
            .order(ActivityTakenEntity_.date, QueryBuilder.DESCENDING
                    or QueryBuilder.CASE_SENSITIVE  ).build()
    }

    override suspend fun insert(activityTakenEntity: ActivityTakenEntity): Long {
        check(activityTakenEntity.activity.targetId != 0L) {
            """
        ActivityTakenEntity.activity relation is missing.

        exportPillId=${activityTakenEntity.exportActivityId}
        targetId=${activityTakenEntity.activity.targetId}

        Setting exportActivityId does NOT initialize the ObjectBox relation.
        Use:

        activityTakenEntity.activity.targetId = dailyActivityEntity.id

        or

        activityTakenEntity.activity.targetId = dailyActivityEntityId
        """.trimIndent()
        }
        return withContext(ioDispatcher) {
            val returnedValue = mActivityTakenDAO.insert(activityTakenEntity)
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }

    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    override suspend fun delete(todoLineable: ActivityTakenEntity): Boolean {
        check(todoLineable.activity.targetId != 0L) {
            """
        ActivityTakenEntity.activity relation is missing.

        exportPillId=${todoLineable.exportActivityId}
        targetId=${todoLineable.activity.targetId}

        Setting exportActivityId does NOT initialize the ObjectBox relation.
        Use:

        activityTakenEntity.activity.targetId = dailyActivityEntity.id

        or

        activityTakenEntity.activity.targetId = dailyActivityEntityId
        """.trimIndent()
        }
        return withContext(ioDispatcher) {
            val returnedValue = mActivityTakenDAO.delete(todoLineable)
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }

    override suspend fun deleteByActivity(dailyActivityEntity: DailyActivityEntity) {
        return withContext(ioDispatcher) {
            val returnedValue = mActivityTakenDAO.deleteByActivity(dailyActivityEntity)
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }

    override suspend fun getActivityTaken(
        dailyActivityEntity: DailyActivityEntity,
        offset: Long,
        limit: Long
    ): Flow<Pair<List<ActivityTakenEntity>, Float?>> {
        return invalidations
            .onStart {
                emit(Unit)
            }
            .map {
                mActivityTakenDAO.getActivityTaken(
                    dailyActivityEntity,
                    offset,
                    limit
                )
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun getActivityTakenList(
        dailyActivityEntity: DailyActivityEntity,
        offset: Long,
        limit: Long
    ): Flow<List<ActivityTakenEntity>> {
        return invalidations
            .onStart {
                emit(Unit)
            }
            .map {
                mActivityTakenDAO.getActivityTakenList(
                    dailyActivityEntity,
                    offset,
                    limit
                )
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun deleteAll() {
        return withContext(ioDispatcher) {
            val returnedValue = mActivityTakenDAO.deleteAll()
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }

    override suspend fun getAllByActivity(dailyActivityEntity: DailyActivityEntity): Flow<List<ActivityTakenEntity>> {
        return  getActivityTakenQuery(dailyActivityEntity).flow()
            .map { it.toList<ActivityTakenEntity>() }
            .flowOn(ioDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAll(): Flow<List<ActivityTakenEntity>> {
        return  mActivityTakenEntityBox.query().build().flow()
            .map { it.toList<ActivityTakenEntity>() }
            .flowOn(ioDispatcher)
    }
}
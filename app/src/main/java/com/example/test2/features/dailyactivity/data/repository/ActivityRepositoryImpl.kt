package com.example.test2.features.dailyactivity.data.repository

import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.features.dailyactivity.data.local.ActivityDAO
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity_
import io.objectbox.Box
import io.objectbox.kotlin.flow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

object ActivityRepositoryImpl : ActivityRepository {
    /**
     * Initializes the DAO with the ObjectBox box instance.
     *
     * Must be called before any other operation.
     *
     * @param box ObjectBox container for DailyActivityEntity persistence.
     */
    private val invalidations = MutableSharedFlow<Unit>()

    private lateinit var mActivityDAO: ActivityDAO
    private lateinit var mDailyActivityBox: Box<DailyActivityEntity>

    private lateinit var ioDispatcher: CoroutineDispatcher
    override fun initialize(
        activityDAO: ActivityDAO,
        dispatcher: CoroutineDispatcher
    ) {
        ioDispatcher = dispatcher
        mActivityDAO = activityDAO
        mDailyActivityBox = mActivityDAO.getBox()
    }

    /**
     * Inserts a new activity into the database.
     *
     * @param newActivity Activity to be persisted.
     * @return The generated database ID.
     * @throws Exception If a unique constraint violation occurs.
     */
    override suspend fun insert(newActivity: DailyActivityEntity): Long {
        return withContext(ioDispatcher) {
            val returnedValue = mActivityDAO.insert(newActivity)
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }

    /**
     * Deletes the specified activity.
     *
     * @param activity Activity to remove.
     * @return true if the activity was removed successfully,
     * false if no matching entity was found.
     */
    override suspend fun delete(activity: DailyActivityEntity): Boolean {
        return withContext(ioDispatcher) {
            val returnedValue = mActivityDAO.delete(activity)
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }

    /**
     * Retrieves all stored activities.
     *
     * Activities are expected to be returned ordered
     * according to the DAO implementation.
     *
     * @return List of all activities.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getActivities(): Flow<List<DailyActivityEntity>> {
        return mDailyActivityBox.query()
            .order(DailyActivityEntity_.hour)
            .order(DailyActivityEntity_.minute)
            .build()
            .flow()
            .map { it.toList<DailyActivityEntity>() }
            .flowOn(ioDispatcher)
    }

    /**
     * Removes all activities from the database.
     */
    override suspend fun deleteAll() {
        return withContext(ioDispatcher) {
            val returnedValue = mActivityDAO.deleteAll()
            invalidations.emit(Unit)
            return@withContext returnedValue
        }
    }
}
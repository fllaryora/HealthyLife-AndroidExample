package com.example.test2.features.recordactivity.data.repository

import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.recordactivity.data.local.ActivityTakenDAO
import com.example.test2.features.recordactivity.data.local.ActivityTakenEntity

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

/**
 * This class is absolutely Framework dependent.
 * DAO for objectbox
 */
interface ActivityTakenRepository  {

    fun initialize(mActivityTakenDAO: ActivityTakenDAO, dispatcher: CoroutineDispatcher) : Unit


    suspend fun insert(activityTakenEntity: ActivityTakenEntity) : Long
    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    suspend fun delete(todoLineable: ActivityTakenEntity) : Boolean

    suspend fun deleteByActivity(dailyActivityEntity: DailyActivityEntity): Unit

    suspend fun getActivityTaken(dailyActivityEntity: DailyActivityEntity, offset: Long, limit: Long) :
            Flow<Pair<List<ActivityTakenEntity>, Float?>>

    suspend fun getActivityTakenList(dailyActivityEntity: DailyActivityEntity, offset: Long, limit: Long) :
            Flow<List<ActivityTakenEntity>>

    suspend fun deleteAll() : Unit

    suspend fun getAllByActivity(dailyActivityEntity: DailyActivityEntity) : Flow<List<ActivityTakenEntity>>

    suspend fun getAll() : Flow<List<ActivityTakenEntity>>
}
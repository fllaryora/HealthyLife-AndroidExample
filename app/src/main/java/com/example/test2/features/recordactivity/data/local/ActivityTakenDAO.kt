package com.example.test2.features.recordactivity.data.local

import com.example.test2.data.dao.behaviors.TodoLineableDAO
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import io.objectbox.Box

/**
 * This class is absolutely Framework dependent.
 * DAO for objectbox
 */
interface ActivityTakenDAO : TodoLineableDAO<ActivityTakenEntity> {

    fun initialize(box: Box<ActivityTakenEntity>)
    fun getBox() :Box<ActivityTakenEntity>


    fun insert(activityTakenEntity: ActivityTakenEntity) : Long

    fun deleteByActivity(dailyActivityEntity: DailyActivityEntity)

    fun getActivityTaken(dailyActivityEntity: DailyActivityEntity, offset: Long, limit: Long) :
            Pair<List<ActivityTakenEntity>, Float?>

    fun getActivityTakenList(dailyActivityEntity: DailyActivityEntity, offset: Long, limit: Long) :
            List<ActivityTakenEntity>

    fun deleteAll()
    fun getAllByActivity(dailyActivityEntity: DailyActivityEntity) : List<ActivityTakenEntity>

    fun getAll() : List<ActivityTakenEntity>
}
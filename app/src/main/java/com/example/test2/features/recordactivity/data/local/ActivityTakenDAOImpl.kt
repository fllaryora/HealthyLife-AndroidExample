package com.example.test2.features.recordactivity.data.local

import com.example.test2.data.converter.TimeConverter
import com.example.test2.data.dao.behaviors.TodoLineableDAO
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import io.objectbox.Box
import io.objectbox.query.Query
import io.objectbox.query.QueryBuilder

/**
 * This class is absolutely Framework dependent.
 * DAO for objectbox
 */
object ActivityTakenDAOImpl : ActivityTakenDAO {

    private lateinit var mActivityTakenEntityBox: Box<ActivityTakenEntity>

    override fun initialize(box: Box<ActivityTakenEntity>) {
        mActivityTakenEntityBox = box
    }
    private fun getActivityTakenQuery(dailyActivityEntity: DailyActivityEntity): Query<ActivityTakenEntity> {
        return mActivityTakenEntityBox.query()
            .equal(ActivityTakenEntity_.activityId, dailyActivityEntity.id)
            .order(ActivityTakenEntity_.date, QueryBuilder.DESCENDING
                    or QueryBuilder.CASE_SENSITIVE  ).build()
    }


    override  fun insert(activityTakenEntity: ActivityTakenEntity) : Long {
        //return the new key
        return mActivityTakenEntityBox.put(activityTakenEntity)
    }

    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    override fun delete(todoLineable: ActivityTakenEntity) : Boolean {
        return mActivityTakenEntityBox.remove(todoLineable)
    }

    override  fun deleteByActivity(dailyActivityEntity: DailyActivityEntity) {
        val list: List<ActivityTakenEntity> = getActivityTakenQuery(dailyActivityEntity).find()
        mActivityTakenEntityBox.remove(list)
    }

    override  fun getActivityTaken(dailyActivityEntity: DailyActivityEntity, offset: Long, limit: Long) :
            Pair<List<ActivityTakenEntity>, Float?> {
        val list: List<ActivityTakenEntity> = getActivityTakenList(dailyActivityEntity,offset,limit)
        val firstTake: Float? = TimeConverter.convertISOToHours(list.firstOrNull()?.getTime())
        return Pair(list, firstTake)
    }

    override  fun getActivityTakenList(dailyActivityEntity: DailyActivityEntity, offset: Long, limit: Long) :
            List<ActivityTakenEntity>{
        val list :List<ActivityTakenEntity> = getActivityTakenQuery(dailyActivityEntity).find(offset,limit)
        return list.sortedBy { activityTakenEntity: ActivityTakenEntity -> activityTakenEntity.date }
    }

    override  fun deleteAll(){
        return mActivityTakenEntityBox.removeAll()
    }

    override  fun getAllByActivity(dailyActivityEntity: DailyActivityEntity) : List<ActivityTakenEntity>{
        return getActivityTakenQuery(dailyActivityEntity).find()
    }

    override  fun getAll() : List<ActivityTakenEntity>{
        return mActivityTakenEntityBox.query().build().find()
    }

    override fun getBox() :Box<ActivityTakenEntity> {
        return mActivityTakenEntityBox
    }
}
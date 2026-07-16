package com.example.test2.data.dao.implementations

import com.example.test2.data.converter.TimeConverter
import com.example.test2.data.dao.behaviors.TodoLineableDAO
import com.example.test2.data.entities.implementations.ActivityTaken
import com.example.test2.data.entities.implementations.ActivityTaken_
import com.example.test2.data.entities.implementations.DailyActivity
import io.objectbox.Box
import io.objectbox.query.Query
import io.objectbox.query.QueryBuilder

/**
 * This class is absolutely Framework dependent.
 * DAO for objectbox
 */
object ActivityTakenDAO : TodoLineableDAO<ActivityTaken> {

    private lateinit var mActivityTakenBox: Box<ActivityTaken>

    fun initialize(box: Box<ActivityTaken>) {
        mActivityTakenBox = box
    }
    private fun getActivityTakenQuery(dailyActivity: DailyActivity): Query<ActivityTaken> {
        return mActivityTakenBox.query()
            .equal(ActivityTaken_.activityId, dailyActivity.id)
            .order(ActivityTaken_.date, QueryBuilder.DESCENDING
                    or QueryBuilder.CASE_SENSITIVE  ).build()
    }


    fun insert(activityTaken: ActivityTaken) : Long {
        //return the new key
        return mActivityTakenBox.put(activityTaken)
    }

    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    override fun delete(todoLineable: ActivityTaken) : Boolean {
        return mActivityTakenBox.remove(todoLineable)
    }

    fun deleteByActivity(dailyActivity: DailyActivity) {
        val list: List<ActivityTaken> = getActivityTakenQuery(dailyActivity).find()
        mActivityTakenBox.remove(list)
    }

    fun getActivityTaken(dailyActivity: DailyActivity, offset: Long, limit: Long) :
            Pair<List<ActivityTaken>, Float?> {
        val list: List<ActivityTaken> = getActivityTakenList(dailyActivity,offset,limit)
        val firstTake: Float? = TimeConverter.convertISOToHours(list.firstOrNull()?.getTime())
        return Pair(list, firstTake)
    }

    fun getActivityTakenList(dailyActivity: DailyActivity, offset: Long, limit: Long) :
            List<ActivityTaken>{
        val list :List<ActivityTaken> = getActivityTakenQuery(dailyActivity).find(offset,limit)
        return list.sortedBy { activityTaken: ActivityTaken -> activityTaken.date }
    }

    fun deleteAll(){
        return mActivityTakenBox.removeAll()
    }

    fun getAllByActivity(dailyActivity: DailyActivity ) : List<ActivityTaken>{
        return getActivityTakenQuery(dailyActivity).find()
    }

    fun getAll() : List<ActivityTaken>{
        return mActivityTakenBox.query().build().find()
    }
}
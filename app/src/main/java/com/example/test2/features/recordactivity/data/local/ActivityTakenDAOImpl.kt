package com.example.test2.features.recordactivity.data.local

import com.example.test2.data.converter.TimeConverter
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
        check(activityTakenEntity.id == 0L) {
            """
        ID is higher or equal to internal ID sequence: 1 (vs. 1). Use ID 0 (zero) to insert new objects.
        
        Should I export the ObjectBox IDs, or should I treat them as internal persistence details?
        No. The ID is infrastructure data.
        date + weight are the business data.
        
        """.trimIndent()
        }
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
        //return the new key
        return mActivityTakenEntityBox.put(activityTakenEntity)
    }

    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    override fun delete(todoLineable: ActivityTakenEntity) : Boolean {
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
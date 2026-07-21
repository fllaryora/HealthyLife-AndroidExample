package com.example.test2.features.dailyactivity.data.local

import com.example.test2.data.dao.helpers.FlatActivities
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import io.objectbox.Box
import io.objectbox.exception.UniqueViolationException
import io.objectbox.query.Query

object ActivityDAOImpl: ActivityDAO {

    private lateinit var mActivityBox: Box<DailyActivityEntity>
    private lateinit var mActivityQuery: Query<DailyActivityEntity>
    private var isInit: Boolean = false


    override fun initialize(box: Box<DailyActivityEntity>) {
        mActivityBox = box
        mActivityQuery = mActivityBox.query()
            .order(DailyActivityEntity_.hour)
            .order(DailyActivityEntity_.minute)
            .build()
        isInit = true
    }



    /**
     * @param newActivity is the new activity to insert in the database
     * @return Pair with the new id and optional DailyActivityEntity to cancel
     */
    override fun insert(newActivity: DailyActivityEntity) : Long {
        if(isInit) {
            try {
                //return the new key
                return mActivityBox.put(newActivity)
            } catch (e: UniqueViolationException) {
                // A User with that name already exists.
                throw Exception(e.message)
            }
        } else {
            throw Exception("ActivityDAOImpl Not init insert")
        }

    }

    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    override fun delete(activity: DailyActivityEntity): Boolean {
        if(isInit) {
            val isSuccessful =  mActivityBox.remove(activity)
            return isSuccessful
        } else {
            throw Exception("ActivityDAOImpl Not init delete")
        }
    }

    override fun getActivities(): List<DailyActivityEntity> {
        if(isInit) {
        return mActivityQuery.find()
        } else {
            throw Exception("ActivityDAOImpl Not init getActivities")
        }
    }

    override fun deleteAll(){
        if(isInit) {
        return mActivityBox.removeAll()
        } else {
            throw Exception("ActivityDAOImpl Not init deleteAll")
        }
    }

    override  fun getBox( ):Box<DailyActivityEntity> {
        if(isInit) {
        return this.mActivityBox
        } else {
            throw Exception("ActivityDAOImpl Not init getBox")
        }
    }
}
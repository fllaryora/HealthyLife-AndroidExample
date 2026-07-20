package com.example.test2.features.dailyactivity.data.local

import com.example.test2.data.dao.helpers.FlatActivities
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import io.objectbox.Box
import io.objectbox.exception.UniqueViolationException
import io.objectbox.query.Query

object ActivityDAOImpl: ActivityDAO {

    private lateinit var mActivityBox: Box<DailyActivityEntity>
    private lateinit var mActivityQuery: Query<DailyActivityEntity>


    override fun initialize(box: Box<DailyActivityEntity>) {
        mActivityBox = box
        mActivityQuery = mActivityBox.query()
            .order(DailyActivityEntity_.hour)
            .order(DailyActivityEntity_.minute)
            .build()
    }



    /**
     * @param newActivity is the new activity to insert in the database
     * @return Pair with the new id and optional DailyActivityEntity to cancel
     */
    override fun insert(newActivity: DailyActivityEntity) : Long {
        try {
            //return the new key
            return mActivityBox.put(newActivity)
        } catch (e: UniqueViolationException) {
            // A User with that name already exists.
            throw Exception(e.message)
        }
    }

    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    override fun delete(activity: DailyActivityEntity): Boolean {
        val isSuccessful =  mActivityBox.remove(activity)
        return isSuccessful
    }

    override fun getActivities(): List<DailyActivityEntity> {
        return mActivityQuery.find()
    }

    override fun deleteAll(){
        return mActivityBox.removeAll()
    }

    override  fun getBox( ):Box<DailyActivityEntity> {
        return this.mActivityBox
    }
}
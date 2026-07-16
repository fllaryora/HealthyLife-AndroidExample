package com.example.test2.data.dao.implementations

import com.example.test2.data.dao.helpers.FlatActivities
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.implementations.DailyActivity
import com.example.test2.data.entities.implementations.DailyActivity_
import io.objectbox.Box
import io.objectbox.exception.UniqueViolationException
import io.objectbox.query.Query

object ActivityDAO {

    private lateinit var mActivityBox: Box<DailyActivity>
    private lateinit var mActivityQuery: Query<DailyActivity>


    fun initialize(box: Box<DailyActivity>) {
        mActivityBox = box
        mActivityQuery = ActivityDAO.mActivityBox.query()
            .order(DailyActivity_.hour)
            .order(DailyActivity_.minute)
            .build()
    }



    /**
     * @param newActivity is the new activity to insert in the database
     * @return Pair with the new id and optional DailyActivity to cancel
     */
    fun insert(newActivity: DailyActivity) : Long {
        try {
            //return the new key
            return ActivityDAO.mActivityBox.put(newActivity)
        } catch (e: UniqueViolationException) {
            // A User with that name already exists.
            throw Exception(e.message)
        }
    }

    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    fun delete(activity: DailyActivity): Boolean {
        val isSuccessful =  ActivityDAO.mActivityBox.remove(activity)
        return isSuccessful
    }

    fun getActivities(): List<DailyActivity> {
        return ActivityDAO.mActivityQuery.find()
    }

    fun getNextActivity(currentHour: Int, currentMinute : Int,
                        currentDayOfWeek: DaysOfWeekEnum
    ): DailyActivity? {
        val activityList:List<DailyActivity> =
            FlatActivities.flatActivities(this.getActivities(), currentDayOfWeek)
        return this.getNextActivityFromList(currentHour, currentMinute ,
            currentDayOfWeek, activityList)
    }

    fun getNextActivityFromList(currentHour: Int, currentMinute : Int,
                                currentDayOfWeek: DaysOfWeekEnum,
                                activityList:List<DailyActivity>): DailyActivity? {
        if(activityList.isEmpty()) {
            return null
        }
        if(activityList.size == 1) {
            return activityList.first()
        }
        //the first task of the day
        var bestMatch:DailyActivity = activityList.first()
        //The distance between currentDayOfWeek and itself always be 0
        val currentTime = currentMinute + (currentHour * 60)
        // + ( 0 *60*24)
        for(thisActivity:DailyActivity in activityList) {
            val distance = currentDayOfWeek.getDistance(DaysOfWeekEnum.getFirstMatch(thisActivity.daysOfWeek))
            val itemTime: Int = (thisActivity.hour*60) + thisActivity.minute +
                    (distance*60*24)
            if(currentTime > itemTime) {
                continue
            }
            bestMatch = thisActivity
            break
        }

        return bestMatch
    }

    fun deleteAll(){
        return ActivityDAO.mActivityBox.removeAll()
    }
}
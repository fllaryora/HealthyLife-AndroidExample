package com.example.test2.features.dailyactivity.data.local

import com.example.test2.data.dao.helpers.FlatActivities
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import io.objectbox.Box
import io.objectbox.exception.UniqueViolationException
import io.objectbox.query.Query

object ActivityDAOImpl {

    private lateinit var mActivityBox: Box<DailyActivityEntity>
    private lateinit var mActivityQuery: Query<DailyActivityEntity>


    fun initialize(box: Box<DailyActivityEntity>) {
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
    fun insert(newActivity: DailyActivityEntity) : Long {
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
    fun delete(activity: DailyActivityEntity): Boolean {
        val isSuccessful =  mActivityBox.remove(activity)
        return isSuccessful
    }

    fun getActivities(): List<DailyActivityEntity> {
        return mActivityQuery.find()
    }

    fun getNextActivity(currentHour: Int, currentMinute : Int,
                        currentDayOfWeek: DaysOfWeekEnum
    ): DailyActivityEntity? {
        val activityList:List<DailyActivityEntity> =
            FlatActivities.flatActivities(this.getActivities(), currentDayOfWeek)
        return this.getNextActivityFromList(currentHour, currentMinute ,
            currentDayOfWeek, activityList)
    }

    fun getNextActivityFromList(currentHour: Int, currentMinute : Int,
                                currentDayOfWeek: DaysOfWeekEnum,
                                activityList:List<DailyActivityEntity>): DailyActivityEntity? {
        if(activityList.isEmpty()) {
            return null
        }
        if(activityList.size == 1) {
            return activityList.first()
        }
        //the first task of the day
        var bestMatch:DailyActivityEntity = activityList.first()
        //The distance between currentDayOfWeek and itself always be 0
        val currentTime = currentMinute + (currentHour * 60)
        // + ( 0 *60*24)
        for(thisActivity:DailyActivityEntity in activityList) {
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
        return mActivityBox.removeAll()
    }
}
package com.example.test2.features.dailyactivity.domain

import com.example.test2.data.dao.helpers.FlatActivities
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.features.dailyactivity.data.local.ActivityDAO
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity

object  ActivityUseCaseImpl : ActivityUseCase {

    private lateinit var mActivityDAO: ActivityDAO
    override fun initialize( mActivityDAO: ActivityDAO) {
        this.mActivityDAO = mActivityDAO
    }

    override fun getNextActivityFromList(currentHour: Int, currentMinute : Int,
                                         currentDayOfWeek: DaysOfWeekEnum,
                                         activityList:List<DailyActivityEntity>): DailyActivityEntity? {
        if(activityList.isEmpty()) {
            return null
        }
        //the first task of the day
        var bestMatch:DailyActivityEntity = activityList.first()

        if(activityList.size == 1) {
            return bestMatch
        }

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

    override fun getNextActivity(currentHour: Int, currentMinute : Int,
                                 currentDayOfWeek: DaysOfWeekEnum
    ): DailyActivityEntity? {
        val activityList:List<DailyActivityEntity> =
            FlatActivities.flatActivities(mActivityDAO.getActivities(), currentDayOfWeek)
        return this.getNextActivityFromList(currentHour, currentMinute ,
            currentDayOfWeek, activityList)
    }
}
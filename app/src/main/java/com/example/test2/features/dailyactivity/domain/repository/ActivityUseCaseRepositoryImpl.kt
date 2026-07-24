package com.example.test2.features.dailyactivity.domain.repository

import com.example.test2.data.dao.helpers.FlatActivities
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.dailyactivity.data.repository.ActivityRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

object ActivityUseCaseRepositoryImpl : ActivityUseCaseRepository {
    /**
     * Initializes the DAO with the ObjectBox box instance.
     *
     * Must be called before any other operation.
     *
     * @param box ObjectBox container for DailyActivityEntity persistence.
     */
    private lateinit var mActivityRepository: ActivityRepository
    private lateinit var ioDispatcher: CoroutineDispatcher
    private var isInit: Boolean = false
    override fun initialize(
        activityRepository: ActivityRepository,
        dispatcher: CoroutineDispatcher
    ) {
        ioDispatcher = dispatcher
        mActivityRepository = activityRepository
        isInit = true
    }

    /**
     * Finds the next activity based on the current date and time.
     *
     * @param currentHour Current hour in 24-hour format.
     * @param currentMinute Current minute.
     * @param currentDayOfWeek Current day of the week.
     *
     * @return The next scheduled activity or null if none exists.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getNextActivity(
        currentHour: Int,
        currentMinute: Int,
        currentDayOfWeek: DaysOfWeekEnum
    ): Flow<DailyActivityEntity?> {

        if(isInit) {
            val activityListFlow: Flow<List<DailyActivityEntity>> =
                FlatActivities.flatActivitiesFlow(
                    mActivityRepository.getActivities(),
                    currentDayOfWeek, ioDispatcher
                )

            return getNextActivityFromList(
                currentHour, currentMinute, currentDayOfWeek,
                activityListFlow
            )
        } else {
            throw Exception("ActivityUseCaseRepositoryImpl Not init getNextActivity")
        }
    }


    /*
    Environment must give the current time in order to be testeable
    * */
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getNextActivityFromList(
        currentHour: Int,
        currentMinute: Int,
        currentDayOfWeek: DaysOfWeekEnum,
        activityListFlow: Flow<List<DailyActivityEntity>>
    ): Flow<DailyActivityEntity?> {
        if(isInit) {
        return activityListFlow
            .mapLatest { activityList: List<DailyActivityEntity> ->
                when {
                    activityList.isEmpty() -> {
                        return@mapLatest null
                    }
                    activityList.size == 1 -> {
                        return@mapLatest activityList.first()
                    }

                    else -> {
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
                        return@mapLatest  bestMatch
                    }
                }
            }
            .flowOn(ioDispatcher)
        } else {
            throw Exception("ActivityUseCaseRepositoryImpl Not init getNextActivityFromList")
        }
    }


}
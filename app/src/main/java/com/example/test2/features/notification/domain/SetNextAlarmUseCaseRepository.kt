package com.example.test2.features.notification.domain

import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.dailyactivity.domain.repository.ActivityUseCaseRepositoryImpl
import com.example.test2.framework.ui.notification.AlarmHelper
import kotlinx.coroutines.flow.first
import java.time.OffsetDateTime

class SetNextAlarmUseCaseRepository(
    private val mAlarmHelper : AlarmHelper) {
    suspend fun setNextAlarm ():Boolean {
        val current = OffsetDateTime.now()
        val currentDayOfWeek: DaysOfWeekEnum = DaysOfWeekEnum.fromDayOfWeek(current.dayOfWeek)
        val nextActivity : DailyActivityEntity? = ActivityUseCaseRepositoryImpl
            .getNextActivity(
                current.hour,
                current.minute,
                currentDayOfWeek
            ).first()
        if(nextActivity != null) {
            return mAlarmHelper.startAlarmManager(nextActivity, current.dayOfYear)
        }
        return false
    }

    suspend fun setNextAlarmAfterCancel ():Boolean {
        val current = OffsetDateTime.now().plusMinutes(1L)
        val currentDayOfWeek: DaysOfWeekEnum = DaysOfWeekEnum.fromDayOfWeek(current.dayOfWeek)
        val nextActivity : DailyActivityEntity? = ActivityUseCaseRepositoryImpl
            .getNextActivity(
                current.hour,
                current.minute,
                currentDayOfWeek
            ).first()
        if(nextActivity != null) {
            return mAlarmHelper.startAlarmManager(nextActivity, current.dayOfYear)
        }
        return false
    }
}
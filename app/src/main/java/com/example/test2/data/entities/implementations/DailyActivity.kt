package com.example.test2.data.entities.implementations

import com.example.test2.data.entities.behaviors.Nameable
import com.example.test2.data.entities.enums.TypeofRecorder
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique
import java.io.Serializable


/**
 * Represents a recurring daily activity and his configuration within the application.
 *
 * The class implements:
 * - [Nameable], allowing the activity to expose a user-friendly label.
 * - [Serializable], enabling serialization for data transfer and persistence.
 *
 * Persistence details:
 * - [id] is the ObjectBox entity identifier and is managed by the database.
 * - [name] is marked as unique to prevent duplicate activity definitions
 *   with the same name.
 *
 * Scheduling details:
 * - [hour] and [minute] define the activity time.
 * - [daysOfWeek] stores the set of days on which the activity is scheduled.
 * - [typeOfRecorder] identifies the category associated
 *   with this activity.
 * - [isAlarmEnabled] indicates whether android notification is enabled for the activity.
 *
 * @property id ObjectBox entity identifier. Must be a mutable `Long` named `id`
 * according to ObjectBox requirements.
 * @property name Unique activity name displayed to the user.
 * @property hour Hour component of the scheduled time.
 * @property minute Minute component of the scheduled time.
 * @property daysOfWeek Encoded representation of the days on which the activity occurs.
 * @property typeOfRecorder A value of TypeofRecorder.
 * @property isAlarmEnabled A flag indicating whether notification is enabled.
 *
 * @see Nameable
 * @see TypeofRecorder
 */

@kotlinx.serialization.Serializable
@Entity
data class DailyActivity (
    @Id
    var id: Long = 0,///must be called id and must be a Long :(
    @Unique
    var name: String,
    var hour: Int,
    var minute: Int,
    var daysOfWeek: Int,
    var typeOfRecorder : Int,
    var isAlarmEnabled:Boolean = false //not used anymore
):Serializable, Nameable {
    /**
     *  @return It returns 0 if otherActivity is equal to this (both of type DailyActivity)
     *  A negative value will be returned if otherActivity is before this.
     *  A positive value will be returned if otherActivity is after this.
     */
    operator fun compareTo(otherActivity: DailyActivity): Int {
        val thisDayInMinutes = this.minute + (this.hour * 60L)
        val otherDayInMinutes = otherActivity.minute + (otherActivity.hour * 60L)
        return (thisDayInMinutes-otherDayInMinutes).toInt()
    }
    override fun getLabel() = name
}
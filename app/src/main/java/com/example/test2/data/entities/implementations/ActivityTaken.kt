package com.example.test2.data.entities.implementations

import com.example.test2.data.entities.behaviors.Graphable
import com.example.test2.data.entities.behaviors.TodoLineable
import com.example.test2.framework.data.database.TimeConverterForKotlinxSerializable
import com.example.test2.framework.data.database.TimeConverterForObjectBox
import io.objectbox.BoxStore
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import java.time.OffsetDateTime

/**
 * Represents the completion of a scheduled [DailyActivity] at a specific
 * point in time.
 *
 * The class implements:
 * - [TodoLineable], allowing the record to be displayed in task or timeline views.
 * - [Graphable], allowing activity ratings to be plotted on charts over time.
 *
 * Relationship details:
 * - Each record is associated with a single [DailyActivity] through an
 *   ObjectBox [ToOne] relation.
 *
 * Persistence details:
 * - [id] is the ObjectBox entity identifier and is managed by the database.
 * - [date] is stored through a custom ObjectBox converter and serialized using
 *   a custom Kotlinx Serialization converter.
 * - [exportActivityId] is the foreign key of the associated [DailyActivity] identifier.
 *
 * @property id ObjectBox entity identifier. Must be a mutable `Long` named `id`
 * according to ObjectBox requirements.
 * @property date Date and time when the activity was performed.
 * @property rating User-provided rating associated with the performance efficiency.
 * @property isTaken Indicates whether the activity was completed or missed. This value is
 * typically always `true`, as non-completed activities are not stored.
 * @property exportActivityId is the [DailyActivity] id.
 *
 * @see DailyActivity
 * @see TodoLineable
 * @see Graphable
 */

@kotlinx.serialization.Serializable
@Entity
data class ActivityTaken (
    @Id
    var id: Long = 0L,///must be called id and must be a Long :(
    @Convert(converter = TimeConverterForObjectBox::class, dbType = String::class)
    // provide default values for all parameters to ensure a default constructor exists
    @kotlinx.serialization.Serializable(with = TimeConverterForKotlinxSerializable::class)
    val date: OffsetDateTime = OffsetDateTime.now(),
    val rating: Int = 0,
    val isTaken: Boolean = true, //always true otherwise the row would not exist
    @Transient
    @kotlinx.serialization.Transient
    var exportActivityId : Long = 0L, //used in export action
) : TodoLineable, Graphable {

    /**
     * Reference to the activity associated with this occurrence.
     */

    @kotlinx.serialization.Transient
    var activity = ToOne<DailyActivity>(this, ActivityTaken_.activity)
    @JvmField
    @Transient
    @Suppress("PropertyName")
    @kotlinx.serialization.Transient
    var __boxStore: BoxStore? = null

    /**
     * Returns the timestamp associated with this activity occurrence.
     */
    override fun getTime() = date

    /**
     * Returns the timestamp used as the X-axis value when plotting
     * activity occurrences on a graph.
     */

    override fun getX() = date

    /**
     * Returns the activity rating used as the Y-axis value when plotting
     * activity data on a graph.
     */

    override fun getY() = rating.toFloat()
    //override fun getLabel() = activity.target.name

    /**
     * Returns the rating associated with this activity occurrence.
     */

    override fun getRatingT() = rating

    /**
     * Indicates that the rating should be displayed when presenting
     * this record.
     */

    override fun getShowRating() = true

    /**
     * Indicates whether the activity was completed.
     */

    override fun isTakenT() = isTaken
}
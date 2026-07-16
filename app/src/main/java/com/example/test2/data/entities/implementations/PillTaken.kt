package com.example.test2.data.entities.implementations

import com.example.test2.data.entities.behaviors.TodoLineable
import com.example.test2.framework.data.database.TimeConverterForKotlinxSerializable
import com.example.test2.framework.data.database.TimeConverterForObjectBox
import io.objectbox.BoxStore
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Transient
import io.objectbox.relation.ToOne
import java.time.OffsetDateTime

/**
 * Represents a pill intake event recorded at a specific point in time.
 *
 * The class implements:
 * - [TodoLineable], allowing the record to be displayed in task, history,
 *   and timeline views.
 *
 * Relationship details:
 * - Each record is associated with a single [Pill] through an ObjectBox
 *   [ToOne] relation.
 *
 * Persistence details:
 * - [id] is the ObjectBox entity identifier and is managed by the database.
 * - [date] is stored through a custom ObjectBox converter and serialized using
 *   a custom Kotlinx Serialization converter.
 * - [exportPillId] is the foreign key of the associated [Pill] identifier.
 *
 * @property id ObjectBox entity identifier. Must be a mutable `Long` named `id`
 * according to ObjectBox requirements.
 * @property date Date and time when the pill was taken.
 * @property isTaken Indicates whether the pill was taken. This value is
 * typically always `true`, as non-taken pills are not stored.
 * @property exportPillId is the foreign key of the associated [Pill] identifier.
 *
 * @see Pill
 * @see TodoLineable
 */

@Entity
@kotlinx.serialization.Serializable
data class PillTaken (
    @Id
    var id: Long = 0L,///must be called id and must be a Long :(
    @Convert(converter = TimeConverterForObjectBox::class, dbType = String::class)
    // provide default values for all parameters to ensure a default constructor exists
    @kotlinx.serialization.Serializable(with = TimeConverterForKotlinxSerializable::class)
    val date: OffsetDateTime = OffsetDateTime.now(),
    val isTaken: Boolean = true, //always true otherwise the row would not exist
    @Transient
    var exportPillId : Long = 0L, //used in export action
) : TodoLineable {
    //ignore
    @kotlinx.serialization.Transient
    var pill = ToOne<Pill>(this, PillTaken_.pill)
    @JvmField
    @Transient
    @Suppress("PropertyName")
    @kotlinx.serialization.Transient
    var __boxStore: BoxStore? = null

    /**
     * Returns the timestamp associated with this pill intake.
     */

    override fun getTime() = date

    /**
     * Returns a fixed rating value used by consumers of the
     * [TodoLineable] interface.
     */

    override fun getRatingT() = if(isTaken) 10 else 0

    /**
     * Indicates that the rating should not be displayed for
     * pill intake records.
     */

    override fun getShowRating() = false

    /**
     * Indicates whether the pill was taken.
     */

    override fun isTakenT() = isTaken
}
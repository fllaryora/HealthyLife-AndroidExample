package com.example.test2.features.recordpill.data.local

import com.example.test2.data.entities.behaviors.TodoLineable
import com.example.test2.features.pill.data.local.PillEntity
import com.example.test2.framework.data.database.TimeConverterForKotlinxSerializable
import com.example.test2.framework.data.database.TimeConverterForObjectBox
import io.objectbox.BoxStore
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Transient
import io.objectbox.relation.ToOne
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

/**
 * Represents a pillEntity intake event recorded at a specific point in time.
 *
 * The class implements:
 * - [com.example.test2.data.entities.behaviors.TodoLineable], allowing the record to be displayed in task, history,
 *   and timeline views.
 *
 * Relationship details:
 * - Each record is associated with a single [com.example.test2.features.pill.data.local.PillEntity] through an ObjectBox
 *   [io.objectbox.relation.ToOne] relation.
 *
 * Persistence details:
 * - [id] is the ObjectBox entity identifier and is managed by the database.
 * - [date] is stored through a custom ObjectBox converter and serialized using
 *   a custom Kotlinx Serialization converter.
 * - [exportPillId] is the foreign key of the associated [com.example.test2.features.pill.data.local.PillEntity] identifier.
 *
 * @property id ObjectBox entity identifier. Must be a mutable `Long` named `id`
 * according to ObjectBox requirements.
 * @property date Date and time when the pillEntity was taken.
 * @property isTaken Indicates whether the pillEntity was taken. This value is
 * typically always `true`, as non-taken pillEntities are not stored.
 * @property exportPillId is the foreign key of the associated [com.example.test2.features.pill.data.local.PillEntity] identifier.
 *
 * @see com.example.test2.features.pill.data.local.PillEntity
 * @see com.example.test2.data.entities.behaviors.TodoLineable
 */

@Entity
@Serializable
data class PillTakenEntity (
    @Id
    var id: Long = 0L,///must be called id and must be a Long :(
    @Convert(converter = TimeConverterForObjectBox::class, dbType = String::class)
    // provide default values for all parameters to ensure a default constructor exists
    @Serializable(with = TimeConverterForKotlinxSerializable::class)
    val date: OffsetDateTime = OffsetDateTime.now(),
    val isTaken: Boolean = true, //always true otherwise the row would not exist
    @Transient
    var exportPillId : Long = 0L, //used in export action
) : TodoLineable {
    //ignore
    @kotlinx.serialization.Transient
    var pillEntity = ToOne<PillEntity>(this, PillTakenEntity_.pillEntity)
    @JvmField
    @Transient
    @Suppress("PropertyName")
    @kotlinx.serialization.Transient
    var __boxStore: BoxStore? = null

    /**
     * Returns the timestamp associated with this pillEntity intake.
     */

    override fun getTime() = date

    /**
     * Returns a fixed rating value used by consumers of the
     * [TodoLineable] interface.
     */

    override fun getRatingT() = if(isTaken) 10 else 0

    /**
     * Indicates that the rating should not be displayed for
     * pillEntity intake records.
     */

    override fun getShowRating() = false

    /**
     * Indicates whether the pillEntity was taken.
     */

    override fun isTakenT() = isTaken
    companion object{
        fun create(pillEntityAsociated: PillEntity, id: Long = 0L, date: OffsetDateTime = OffsetDateTime.now(), isTaken: Boolean = true):PillTakenEntity {
            return PillTakenEntity (id = id, date= date, isTaken = isTaken).apply {
                pillEntity.target = pillEntityAsociated
            }
        }
    }
}


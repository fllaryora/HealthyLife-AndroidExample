package com.example.test2.features.recordactivity.data.local

import com.example.test2.data.entities.behaviors.Graphable
import com.example.test2.data.entities.behaviors.ImportableTaken
import com.example.test2.data.entities.behaviors.TodoLineable
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.framework.data.database.TimeConverterForKotlinxSerializable
import com.example.test2.framework.data.database.TimeConverterForObjectBox
import io.objectbox.BoxStore
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.OffsetDateTime

/**
 * Represents the completion of a scheduled [com.example.test2.features.dailyactivity.data.local.DailyActivityEntity] at a specific
 * point in time.
 *
 * The class implements:
 * - [com.example.test2.data.entities.behaviors.TodoLineable], allowing the record to be displayed in task or timeline views.
 * - [com.example.test2.data.entities.behaviors.Graphable], allowing activity ratings to be plotted on charts over time.
 *
 * Relationship details:
 * - Each record is associated with a single [com.example.test2.features.dailyactivity.data.local.DailyActivityEntity] through an
 *   ObjectBox [io.objectbox.relation.ToOne] relation.
 *
 * Persistence details:
 * - [id] is the ObjectBox entity identifier and is managed by the database.
 * - [date] is stored through a custom ObjectBox converter and serialized using
 *   a custom Kotlinx Serialization converter.
 * - [exportActivityId] is the foreign key of the associated [com.example.test2.features.dailyactivity.data.local.DailyActivityEntity] identifier.
 *
 * @property id ObjectBox entity identifier. Must be a mutable `Long` named `id`
 * according to ObjectBox requirements.
 * @property date Date and time when the activity was performed.
 * @property rating User-provided rating associated with the performance efficiency.
 * @property isTaken Indicates whether the activity was completed or missed. This value is
 * typically always `true`, as non-completed activities are not stored.
 * @property exportActivityId is the [com.example.test2.features.dailyactivity.data.local.DailyActivityEntity] id.
 *
 * @see com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
 * @see com.example.test2.data.entities.behaviors.TodoLineable
 * @see com.example.test2.data.entities.behaviors.Graphable
 */


@Entity
@Serializable(with = ActivityTakenEntity.Companion.Serializer::class)
data class ActivityTakenEntity (
    @Id
    var id: Long = 0L,///must be called id and must be a Long :(
    @Convert(converter = TimeConverterForObjectBox::class, dbType = String::class)
    // provide default values for all parameters to ensure a default constructor exists
    @Serializable(with = TimeConverterForKotlinxSerializable::class)
    val date: OffsetDateTime = OffsetDateTime.now(),
    val rating: Int = 0,
    val isTaken: Boolean = true, //always true otherwise the row would not exist
    @Transient // will not be store in database
    var exportActivityId : Long = 0L, //used in export action
) : TodoLineable, Graphable, ImportableTaken<ActivityTakenEntity, DailyActivityEntity, Long> {

    /**
     * Reference to the activity associated with this occurrence.
     */

    @kotlinx.serialization.Transient
    var activity = ToOne<DailyActivityEntity>(this, ActivityTakenEntity_.activity)
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

    override fun prepareForImport(owner: DailyActivityEntity): ActivityTakenEntity {
        return copy(id = 0L).apply {
            //used in import action
            activity.target = owner
            //used in export action
            exportActivityId = owner.id
        }
    }

    override fun getOwnerId(): Long {
        return exportActivityId
    }

    companion object{
        fun create(activityEntityAsociated: DailyActivityEntity, id: Long = 0L, date: OffsetDateTime = OffsetDateTime.now(), rating: Int = 0, isTaken: Boolean = true):ActivityTakenEntity {
            return ActivityTakenEntity (id = id, date= date, rating = rating, isTaken = isTaken).apply {
                activity.target = activityEntityAsociated
                //used in export action
                exportActivityId = activityEntityAsociated.id
            }
        }
        object Serializer : KSerializer<ActivityTakenEntity> {

            private val dateSerializer =
                TimeConverterForKotlinxSerializable()

            override val descriptor: SerialDescriptor =
                buildClassSerialDescriptor("ActivityTakenEntity") {
                    element<Long>("id")
                    element("date", dateSerializer.descriptor)
                    element<Int>("rating")
                    element<Boolean>("isTaken")
                    element<Long>("exportActivityId")
                }

            override fun serialize(
                encoder: Encoder,
                value: ActivityTakenEntity
            ) {
                val composite = encoder.beginStructure(descriptor)

                composite.encodeLongElement(
                    descriptor,
                    0,
                    value.id
                )

                composite.encodeSerializableElement(
                    descriptor,
                    1,
                    dateSerializer,
                    value.date
                )

                composite.encodeIntElement(
                    descriptor,
                    2,
                    value.rating
                )

                composite.encodeBooleanElement(
                    descriptor,
                    3,
                    value.isTaken
                )

                // IMPORTANT:
                // exportActivityId is transient and therefore is not persisted by
                // ObjectBox. After loading the entity with getAll(), its value is
                // reconstructed as 0. For export purposes, use activity.targetId,
                // which contains the actual foreign key managed by ObjectBox.
                composite.encodeLongElement(
                    descriptor,
                    4,
                    value.activity.targetId
                )

                composite.endStructure(descriptor)
            }

            override fun deserialize(
                decoder: Decoder
            ): ActivityTakenEntity {

                val composite =
                    decoder.beginStructure(descriptor)

                var id = 0L
                var date = OffsetDateTime.now()
                var rating = 0
                var isTaken = true
                var exportActivityId = 0L

                loop@ while (true) {
                    when (
                        composite.decodeElementIndex(descriptor)
                    ) {
                        CompositeDecoder.DECODE_DONE ->
                            break@loop

                        0 ->
                            id = composite.decodeLongElement(
                                descriptor,
                                0
                            )

                        1 ->
                            date = composite.decodeSerializableElement(
                                descriptor,
                                1,
                                dateSerializer
                            )

                        2 ->
                            rating = composite.decodeIntElement(
                                descriptor,
                                2
                            )

                        3 ->
                            isTaken = composite.decodeBooleanElement(
                                descriptor,
                                3
                            )

                        4 ->
                            exportActivityId =
                                composite.decodeLongElement(
                                    descriptor,
                                    4
                                )

                        else ->
                            throw SerializationException(
                                "Unexpected index"
                            )
                    }
                }

                composite.endStructure(descriptor)

                return ActivityTakenEntity(
                    id = id,
                    date = date,
                    rating = rating,
                    isTaken = isTaken,
                    exportActivityId = exportActivityId
                )
            }
        }
    }
}
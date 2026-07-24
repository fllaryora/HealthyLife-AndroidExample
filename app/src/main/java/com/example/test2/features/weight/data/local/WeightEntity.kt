package com.example.test2.features.weight.data.local

import com.example.test2.data.entities.behaviors.Graphable
import com.example.test2.data.entities.behaviors.Importable
import com.example.test2.data.entities.behaviors.Timelineable
import com.example.test2.framework.data.database.TimeConverterForKotlinxSerializable
import com.example.test2.framework.data.database.TimeConverterForObjectBox
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.util.Locale


/**
 * Represents a weight measurement recorded at a specific point in time.
 *
 * The class implements:
 * - [Graphable], allowing weight data to be represented in charts where
 *   the X axis corresponds to the measurement date and the Y axis to the weight value.
 * - [Timelineable], allowing weight measurements to be displayed in chronological views.
 *
 * Persistence details:
 * - [id] is the ObjectBox entity identifier and is managed by the database.
 * - [date] is stored through a custom ObjectBox converter and serialized using
 *   a custom Kotlinx Serialization converter.
 * - [date] is marked as unique to ensure only one measurement exists for a given timestamp.
 *
 * @property id ObjectBox entity identifier. Must be a mutable `Long` named `id`
 * according to ObjectBox requirements.
 * @property date Date and time when the weight measurement was recorded.
 * @property weight Weight value expressed in the application's configured unit.
 *
 * @see Graphable
 * @see Timelineable
 */

@Entity
@Serializable
data class WeightEntity (
    @Id
    var id: Long = 0L,///must be called id and must be a Long :(
    @Convert(converter = TimeConverterForObjectBox::class, dbType = String::class)
    @Serializable(with = TimeConverterForKotlinxSerializable::class)
    @Unique
    val date: OffsetDateTime,
    val weight: Float
): Graphable, Timelineable, Importable<WeightEntity, OffsetDateTime> {
    /**
     * Returns the timestamp used as the X-axis value when plotting this
     * measurement in a graph.
     */
    override fun getX() = date
    /**
     * Returns the weight value used as the Y-axis value when plotting this
     * measurement in a graph.
     */
    override fun getY() = weight
    /**
     * Returns the timestamp associated with this measurement for timeline
     * visualization purposes.
     */
    override fun getTime() = date
    /**
     * Returns a localized string representation of the weight value formatted
     * with two decimal places.
     */
    override fun getLabel() = String.format(Locale.getDefault(),"%.2f", weight)

    override fun prepareForImport(): WeightEntity = copy(id = 0L)
    override fun importSortKey(): OffsetDateTime = date
    override fun getIdForImport(): Long {
       return id
    }

    override fun setIdForImport(newId: Long) {
        id = newId
    }
}
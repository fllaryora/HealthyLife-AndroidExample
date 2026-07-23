package com.example.test2.features.water.data.local

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
 * Represents a water intake event recorded at a specific point in time.
 *
 * The class implements:
 * - [com.example.test2.data.entities.behaviors.Graphable], allowing water intake data to be plotted on charts where
 *   the X axis corresponds to the intake date and the Y axis to the consumed volume.
 * - [com.example.test2.data.entities.behaviors.Timelineable], allowing intake events to be displayed in chronological views.
 *
 * Persistence details:
 * - [id] is the ObjectBox entity identifier and is managed by the database.
 * - [date] is stored through a custom ObjectBox converter and serialized using
 *   a custom Kotlinx Serialization converter.
 * - [date] is marked as unique to ensure only one intake record exists for a
 *   given timestamp.
 *
 * @property id ObjectBox entity identifier. Must be a mutable `Long` named `id`
 * according to ObjectBox requirements.
 * @property date Date and time when the water intake was recorded.
 * @property volume Amount of water consumed during the intake event.
 *
 * @see com.example.test2.data.entities.behaviors.Graphable
 * @see com.example.test2.data.entities.behaviors.Timelineable
 */

    @Entity
    @Serializable
    data class WaterEntity (
    @Id
        var id: Long = 0,///must be called id and must be a Long :(
    @Convert(converter = TimeConverterForObjectBox::class, dbType = String::class)
        @Serializable(with = TimeConverterForKotlinxSerializable::class)
        @Unique
        val date: OffsetDateTime,
        val volume: Float
    ): Graphable, Timelineable, Importable<WaterEntity, OffsetDateTime>{

    /**
     * Returns the timestamp used as the X-axis value when plotting this
     * water intake in a graph.
     */

    override fun getX() = date
    /**
     * Returns the consumed volume used as the Y-axis value when plotting
     * this water intake in a graph.
     */
    override fun getY() = volume

    /**
     * Returns the timestamp associated with this intake event for timeline
     * visualization purposes.
     */

    override fun getTime() = date

    /**
     * Returns a localized string representation of the consumed volume
     * formatted with two decimal places.
     */

    override fun getLabel() = String.format(Locale.getDefault(),"%.2f", volume)
    override fun prepareForImport(): WaterEntity = copy(id = 0L)
    override fun importSortKey(): OffsetDateTime = date
    }
package com.example.test2.data.entities.implementations

import com.example.test2.data.entities.behaviors.Nameable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique
import java.io.Serializable


/**
 * Represents a pill or medication that can be tracked by the application.
 *
 * The class implements:
 * - [Nameable], allowing the pill to expose a user-friendly label.
 * - [Serializable], enabling serialization for data transfer and persistence.
 *
 * Persistence details:
 * - [id] is the ObjectBox entity identifier and is managed by the database.
 * - [name] is marked as unique to ensure that only one pill definition exists
 *   for a given name.
 *
 * @property id ObjectBox entity identifier. Must be a mutable `Long` named `id`
 * according to ObjectBox requirements.
 * @property name Human-readable name of the pill or medication.
 *
 * @see Nameable
 */

@Entity
@kotlinx.serialization.Serializable
data class Pill (
    @Id
    var id: Long = 0,///must be called id and must be a Long :(
    @Unique
    val name: String
) : Serializable, Nameable {

    /**
     * Returns the display label associated with this pill.
     */

    override fun getLabel() = name
}
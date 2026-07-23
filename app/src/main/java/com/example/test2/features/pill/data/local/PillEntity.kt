package com.example.test2.features.pill.data.local

import com.example.test2.data.entities.behaviors.Importable
import com.example.test2.data.entities.behaviors.Nameable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique
import kotlinx.serialization.Serializable

/**
 * Represents a pillEntity or medication that can be tracked by the application.
 *
 * The class implements:
 * - [com.example.test2.data.entities.behaviors.Nameable], allowing the pillEntity to expose a user-friendly label.
 * - [java.io.Serializable], enabling serialization for data transfer and persistence.
 *
 * Persistence details:
 * - [id] is the ObjectBox entity identifier and is managed by the database.
 * - [name] is marked as unique to ensure that only one pillEntity definition exists
 *   for a given name.
 *
 * @property id ObjectBox entity identifier. Must be a mutable `Long` named `id`
 * according to ObjectBox requirements.
 * @property name Human-readable name of the pillEntity or medication.
 *
 * @see com.example.test2.data.entities.behaviors.Nameable
 */

@Entity
@Serializable
data class PillEntity (
    @Id
    var id: Long = 0,///must be called id and must be a Long :(
    @Unique
    val name: String
) : java.io.Serializable, Nameable, Importable<PillEntity, String> {

    /**
     * Returns the display label associated with this pillEntity.
     */

    override fun getLabel() = name
    override fun prepareForImport(): PillEntity {
        return copy(id = 0L)
    }

    override fun importSortKey(): String {
        return name
    }
}